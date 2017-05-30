package cn.omsfuk.smart.framework.core;

import cn.omsfuk.smart.framework.core.annotation.BeanScope;

/**
 * Created by omsfuk on 17-5-29.
 */
public interface BeanContext {

    Object getBean(String name);

    Object getBean(Class<?> beanClass);

    void setBean(String name, Class<?> beanClass, BeanScope beanScope);

    void setBean(String name, Object obj, BeanScope beanScope);

    void removeRequestBeans();

}
