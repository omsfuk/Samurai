package cn.omsfuk.smart.framework.core;

import cn.omsfuk.smart.framework.core.annotation.BeanScope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by omsfuk on 17-5-29.
 */
public interface BeanContext {

    Object getBean(String name);

    Object getBean(Class<?> beanClass);

    void setBean(String name, Constructor constructor, BeanScope beanScope);

    void setBean(String name, Object configObject, Method method, BeanScope beanScope);

    void setBean(String name, Class<?> beanClass, BeanScope beanScope);

    void setBean(String name, Object instance, BeanScope beanScope);

    void removeRequestBeans();

}
