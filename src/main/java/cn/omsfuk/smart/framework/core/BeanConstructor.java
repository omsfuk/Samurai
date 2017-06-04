package cn.omsfuk.smart.framework.core;

import cn.omsfuk.smart.framework.core.exception.InstanceBeanException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Created by omsfuk on 17-6-4.
 */
public class BeanConstructor {

    private Object configObject;

    private Class<?>[] paramTypes;

    private Function<Object[], Object> constructor;

    public BeanConstructor(Constructor construcor) {
        this.constructor = convertConstructorToNormalBeanConstructor(construcor);
        setParamTypes(construcor.getParameterTypes());
    }

    public BeanConstructor(Object configObject, Method method) {
        this.configObject = configObject;
        this.constructor = convertMethodToNormalBeanConstructor(method);
        setParamTypes(method.getParameterTypes());
    }

    private Function<Object[], Object> convertConstructorToNormalBeanConstructor(Constructor constructor) {
        return (args) -> {
            try {
                return constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new InstanceBeanException(e);
            }
        };
    }

    private Function<Object[], Object> convertMethodToNormalBeanConstructor(Method method) {
        return (args) -> {
            try {
                return method.invoke(getConfigObject(), args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InstanceBeanException(e);
            }
        };
    }

    public Object construct(Object... args) {
        try {
            return constructor.apply(args);
        } catch (Exception e) {
            throw new InstanceBeanException(e);
        }
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object getConfigObject() {
        return configObject;
    }

    public void setConfigObject(Object configObject) {
        this.configObject = configObject;
    }
}
