package cn.omsfuk.smart.framework.core.bean;

import cn.omsfuk.smart.framework.core.annotation.Bean;
import cn.omsfuk.smart.framework.core.annotation.BeanScope;

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

    private BeanScope beanScope;

    public AbstractBean(String name, Constructor constructor, BeanScope beanScope) {
        this.name = name;
        this.constructor = new BeanConstructor(constructor);
        this.beanType = constructor.getDeclaringClass();
        this.beanScope = beanScope;
    }

    public AbstractBean(String name, Object configObject, Method method, BeanScope beanScope) {
        this.name = name;
        this.constructor = new BeanConstructor(configObject, method);
        this.beanType = method.getReturnType();
        this.beanScope = beanScope;
    }

    public AbstractBean(String name, Object instance, BeanScope beanScope) {
        this.name = name;
        this.instance = instance;
        this.beanType = instance.getClass();
        this.beanScope = beanScope;
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

    public BeanScope getBeanScope() {
        return beanScope;
    }
}
