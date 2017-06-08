package cn.omsfuk.samurai.framework.core.bean;

import cn.omsfuk.samurai.framework.core.annotation.BeanScope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * BeanContext内部bean的存储结构。
 * Created by omsfuk on 17-6-2.
 */
public class AbstractBean {

    /**
     * bean名称
     */
    private String name;

    /**
     * bean类型
     */
    private Class<?> beanType;

    /**
     * 统一的构造器。
     */
    private BeanConstructor constructor;

    /**
     * 实例。可以被缓存
     */
    private Object instance;

    /**
     * Bean 作用域
     */
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
