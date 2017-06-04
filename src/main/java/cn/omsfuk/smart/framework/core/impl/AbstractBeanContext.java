package cn.omsfuk.smart.framework.core.impl;

import cn.omsfuk.smart.framework.core.BeanContext;
import cn.omsfuk.smart.framework.core.BeanScanner;
import cn.omsfuk.smart.framework.core.ProxyChain;
import cn.omsfuk.smart.framework.core.annotation.*;
import cn.omsfuk.smart.framework.core.exception.InstanceBeanException;
import cn.omsfuk.smart.framework.helper.AnnotationHelper;
import cn.omsfuk.smart.framework.helper.CgLibUtil;
import cn.omsfuk.smart.framework.helper.ClassHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-6-2.
 */
public abstract class AbstractBeanContext implements BeanContext, BeanScanner {

    @Override
    public List<Class<?>> scanBeanByAnnotation(List<Class<? extends Annotation>> annoList, String... packages) {
        return ClassHelper.loadClassByAnnotation(annoList, packages);
    }

    @Override
    public List<Class<?>> scanBeanByAnnotation(Class<? extends Annotation> annotation, String... packages) {
        return ClassHelper.loadClassByAnnotation(annotation, packages);
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

    protected void fillAspect(Map<String, Class<?>> beanMap, List<ProxyChain> proxys) {
        beanMap.forEach((key, value) -> {
            BeanScope beanScope = BeanScope.singleton;
            if (value.isAnnotationPresent(Scope.class)) {
                beanScope = value.getAnnotation(Scope.class).value();
            }
            Class proxyClass = CgLibUtil.getProxy(value, proxys.stream()
                    .filter(proxy -> isClassMatch(proxy.getClassName(), value.getName()) || value.isAnnotationPresent(proxy.getAnnotation()))
                    .collect(Collectors.toList()));
            beanMap.put(key, proxyClass);
            setBean(key, proxyClass, beanScope);
        });
    }

    protected static boolean isClassMatch(String pattern, String name) {
        return name.matches(pattern);
    }
}
