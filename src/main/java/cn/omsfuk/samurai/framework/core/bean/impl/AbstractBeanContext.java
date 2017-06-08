package cn.omsfuk.samurai.framework.core.bean.impl;

import cn.omsfuk.samurai.framework.core.annotation.*;
import cn.omsfuk.samurai.framework.core.bean.AbstractBean;
import cn.omsfuk.samurai.framework.core.bean.BeanContext;
import cn.omsfuk.samurai.framework.core.bean.BeanScanner;
import cn.omsfuk.samurai.framework.core.exception.MoreThanOneConstructorException;
import cn.omsfuk.samurai.framework.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 抽象的beanContext，实现了一些公用的方法
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
        ClassUtil.loadClassByAnnotation(annoList, packages).forEach(beanClass -> {
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
        ClassUtil.loadClassByAnnotation(annotation, packages).forEach(beanClass -> {
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
        ClassUtil.loadClassByAnnotation(annotations, packages).forEach(beanClass -> {
            if (beanClass.getConstructors().length != 1) {
                throw new MoreThanOneConstructorException();
            }
            beans.add(new AbstractBean(getBeanId(beanClass), beanClass.getConstructors()[0], resolveBeanScope(beanClass)));
        });
        ClassUtil.loadClassByAnnotation(Config.class, packages).forEach(configClass -> {
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
        return ClassUtil.loadClassByAnnotation(Aspect.class, packages);
    }

    private String getBeanId(Method method) {
        Bean beanAnno = method.getAnnotation(Bean.class);
        return beanAnno.value();
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

    private BeanScope resolveBeanScope(Class<?> cls) {
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
}
