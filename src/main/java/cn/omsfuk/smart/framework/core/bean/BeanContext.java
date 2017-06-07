package cn.omsfuk.smart.framework.core.bean;

import cn.omsfuk.smart.framework.core.annotation.BeanScope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by omsfuk on 17-5-29.
 */
public interface BeanContext {

    /**
     * 通过beanid获取bean
     * @param name
     * @return
     */
    Object getBean(String name);

    /**
     * 通过类获取bean
     * @param beanClass
     * @return
     */
    Object getBean(Class<?> beanClass);

    /**
     * 通过普通方法添加bean
     * @param name
     * @param configObject
     * @param method
     * @param beanScope
     */
    void setBean(String name, Object configObject, Method method, BeanScope beanScope);

    /**
     * 通过类添加bean
     * @param name
     * @param beanClass
     * @param beanScope
     */
    void setBean(String name, Class<?> beanClass, BeanScope beanScope);

    void setBean(String name, Object instance, BeanScope beanScope);

    void removeRequestBeans();

}
