package cn.omsfuk.samurai.framework.core.bean;

import cn.omsfuk.samurai.framework.core.exception.InstanceBeanException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * bean构造器。可以使constructor，也可以是普通的方法
 * Created by omsfuk on 17-6-4.
 */
public class BeanConstructor {

    /**
     * 如果是普通的方法，那么规定一定要有一个依托的实例对象。
     */
    private Object configObject;

    /**
     * 所需参数。
     */
    private Class<?>[] paramTypes;

    /**
     * 存储构造器
     */
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

}
