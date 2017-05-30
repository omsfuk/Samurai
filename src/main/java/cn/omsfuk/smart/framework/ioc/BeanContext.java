package cn.omsfuk.smart.framework.ioc;

import cn.omsfuk.smart.framework.ioc.annotation.BeanScope;

/**
 * Created by omsfuk on 17-5-29.
 */
public interface BeanContext {

    Object getBean(String name);

    Object getBean(Class<?> beanClass);

    void setBean(String name, Class<?> beanClass, BeanScope beanScope);

    void removeRequestBeans();

}
