package cn.omsfuk.smart.framework.core.bean.impl;

import cn.omsfuk.smart.framework.core.aop.GeneticAspect;
import cn.omsfuk.smart.framework.core.aop.ProxyFactory;
import cn.omsfuk.smart.framework.core.bean.AbstractBean;
import cn.omsfuk.smart.framework.core.bean.BeanConstructor;
import cn.omsfuk.smart.framework.core.bean.BeanContext;
import cn.omsfuk.smart.framework.core.bean.BeanScanner;
import cn.omsfuk.smart.framework.core.annotation.*;
import cn.omsfuk.smart.framework.core.exception.InstanceBeanException;
import cn.omsfuk.smart.framework.core.exception.MoreThanOneConstructorException;
import cn.omsfuk.smart.framework.helper.AnnotationHelper;
import cn.omsfuk.smart.framework.helper.ClassHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-6-2.
 */
public abstract class AbstractBeanContext implements BeanContext, BeanScanner {

    /**
     * 扫描bean，不包括bean注解下的bean
     * @param annoList
     * @param packages
     * @return
     */
    @Override
    public List<AbstractBean> scanBeanByAnnotation(List<Class<? extends Annotation>> annoList, String... packages) {
        List<AbstractBean> beans = new LinkedList<>();
        ClassHelper.loadClassByAnnotation(annoList, packages).forEach(beanClass -> {
            if (beanClass.getConstructors().length != 1) {
                throw new MoreThanOneConstructorException();
            }
            beans.add(new AbstractBean(getBeanId(beanClass), beanClass.getConstructors()[0], resolveBeanScope(beanClass)));
        });
        return beans;
    }

    /**
     * 扫描bean，不包括bean注解下的bean
     * @param annotation
     * @param packages
     * @return
     */
    @Override
    public List<AbstractBean> scanBeanByAnnotation(Class<? extends Annotation> annotation, String... packages) {
        List<AbstractBean> beans = new LinkedList<>();
        ClassHelper.loadClassByAnnotation(annotation, packages).forEach(beanClass -> {
            if (beanClass.getConstructors().length != 1) {
                throw new MoreThanOneConstructorException();
            }
            beans.add(new AbstractBean(getBeanId(beanClass), beanClass.getConstructors()[0], resolveBeanScope(beanClass)));
        });
        return beans;
    }

    /**
     * 扫描bean，包括Bean注解下的bean。
     * @param packages
     * @return
     */
    @Override
    public List<AbstractBean> scanBean(String... packages) {
        List<Class<? extends Annotation>> annotations = new LinkedList<>();
        List<AbstractBean> beans = new LinkedList<>();
        annotations.add(Controller.class);
        annotations.add(Service.class);
        annotations.add(Component.class);
        ClassHelper.loadClassByAnnotation(annotations, packages).forEach(beanClass -> {
            if (beanClass.getConstructors().length != 1) {
                throw new MoreThanOneConstructorException();
            }
            beans.add(new AbstractBean(getBeanId(beanClass), beanClass.getConstructors()[0], resolveBeanScope(beanClass)));
        });
        ClassHelper.loadClassByAnnotation(Config.class, packages).forEach(configClass -> {
            Stream.of(configClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Bean.class))
                    .forEach(method -> {
                        beans.add(new AbstractBean(getBeanId(method), configClass, method, resolveBeanScope(method)));
                    });
        });
        return beans;
    }

    @Override
    public List<Class<?>> scanAspect(String... packages) {
        return ClassHelper.loadClassByAnnotation(Aspect.class, packages);
    }

    private BeanScope resolveBeanScope(Class<?> cls) {
        Bean beanAnno = null;
        if (cls.isAnnotationPresent(Component.class)) {
            return cls.getAnnotation(Component.class).scope();
        }
        if (cls.isAnnotationPresent(Controller.class)) {
            return cls.getAnnotation(Controller.class).scope();
        }
        if (cls.isAnnotationPresent(Service.class)) {
            return cls.getAnnotation(Service.class).scope();
        }
        return null;
    }

    private BeanScope resolveBeanScope(Method method) {
        Bean beanAnno = method.getAnnotation(Bean.class);
        return beanAnno.scope();
    }

    protected Object satisfyFieldDenpendencies(Object bean) {
        Class<?> beanClass = AnnotationHelper.getOriginClass(bean.getClass());
        Stream.of(beanClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> {
                    String beanType = field.getAnnotation(Inject.class).value();
                    try {
                        field.setAccessible(true);
                        if ("".equals(beanType)) {
                            field.set(bean, getBean(field.getType()));
                        } else {
                            field.set(bean, getBean(beanType));
                        }
                    } catch (IllegalAccessException e) {
                        // TODO 异常处理
                        throw new RuntimeException(e);
                    }
                });
        return bean;
    }

    protected Object getInstance(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getConstructors();
        if (constructors.length != 1) {
            InstanceBeanException instanceBeanException = new InstanceBeanException("more than one constructor or less [" + beanClass.getName() + "]");
            throw instanceBeanException;
        }

        Constructor<?> constructor = constructors[0];
        Object[] params = new Object[constructor.getParameterCount()];
        Class<?>[] paramTypes = constructor.getParameterTypes();
        String beanId = beanClass.getSimpleName();
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            params[i] = getBean(paramTypes[i]);
        }

        Object instance = null;
        try {
            instance = constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            InstanceBeanException instanceBeanException = new InstanceBeanException("constructor params can't be satisfied [" + beanClass.getName() + "]");
            throw instanceBeanException;
        }

        return satisfyFieldDenpendencies(instance);
    }

    protected void weaveAspect(List<AbstractBean> beans, List<GeneticAspect> aspects) {
        beans.forEach(bean -> {
            Class<?> proxyClass = ProxyFactory.weaveAspect(bean.getBeanType(), findMatchedAspect(bean.getBeanType(), aspects));
            bean.setConstructor(new BeanConstructor(proxyClass.getConstructors()[0]));
        });
    }

    protected List<GeneticAspect> findMatchedAspect(Class<?> beanClass, List<GeneticAspect> aspects) {
        return aspects.stream()
                .filter(aspect -> isClassMatch(aspect.getWeavingClass(), beanClass.getName()) || beanClass.isAnnotationPresent(aspect.getWeavingAnnotation()))
                .collect(Collectors.toList());
    }

    protected static boolean isClassMatch(String pattern, String name) {
        return name.matches(pattern);
    }

    private String getBeanId(Class<?> beanClass) {
        String beanId = "";
        if (beanClass.isAnnotationPresent(Component.class) && !(beanId = beanClass.getAnnotation(Component.class).value()).equals("")) {
            return beanId;
        }
        if (beanClass.isAnnotationPresent(Controller.class) && !(beanId = beanClass.getAnnotation(Controller.class).value()).equals("")) {
            return beanId;
        }
        if (beanClass.isAnnotationPresent(Repository.class) && !(beanId = beanClass.getAnnotation(Repository.class).value()).equals("")) {
            return beanId;
        }
        if (beanClass.isAnnotationPresent(Service.class) && !(beanId = beanClass.getAnnotation(Service.class).value()).equals("")) {
            return beanId;
        }
        return beanClass.getSimpleName();
    }

    private String getBeanId(Method method) {
        Bean beanAnno = method.getAnnotation(Bean.class);
        return beanAnno.value();
    }
}
