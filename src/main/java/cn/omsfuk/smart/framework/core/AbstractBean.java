package cn.omsfuk.smart.framework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by omsfuk on 17-6-2.
 */
public class AbstractBean {

    private String name;

    private Class<?> beanType;

    private BeanConstructor constructor;

    private Object instance;

    public AbstractBean(String name, Constructor constructor) {
        this.name = name;
        this.constructor = new BeanConstructor(constructor);
        this.beanType = constructor.getDeclaringClass();
    }

    public AbstractBean(String name, Object configObject, Method method) {
        this.name = name;
        this.constructor = new BeanConstructor(configObject, method);
        this.beanType = method.getReturnType();
    }

    public AbstractBean(String name, Object instance) {
        this.name = name;
        this.instance = instance;
        this.beanType = instance.getClass();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BeanConstructor getConstructor() {
        return constructor;
    }

    public void setConstructor(BeanConstructor constructor) {
        this.constructor = constructor;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }
}
