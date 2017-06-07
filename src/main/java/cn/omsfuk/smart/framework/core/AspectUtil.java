package cn.omsfuk.smart.framework.core;

import cn.omsfuk.smart.framework.core.annotation.After;
import cn.omsfuk.smart.framework.core.annotation.Around;
import cn.omsfuk.smart.framework.core.annotation.Before;
import cn.omsfuk.smart.framework.core.aop.GeneticAspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by omsfuk on 17-6-5.
 */
public class AspectUtil {

    public static boolean isAspectMethod(Method method) {
        if (method.isAnnotationPresent(Before.class)
                || method.isAnnotationPresent(After.class)
                || method.isAnnotationPresent(Around.class)) {
            return true;
        }
        return false;
    }

    public static GeneticAspect getAspect(Object aspectObject, Method method, int order) {
        GeneticAspect aspect = null;
        if (method.isAnnotationPresent(Before.class)) {
            Before before = method.getAnnotation(Before.class);
            aspect = new GeneticAspect(before.value(), before.method(), before.anno(), order);
            aspect.setBefore(() -> invokeMethod(aspectObject, method, null));
        } else if (method.isAnnotationPresent(After.class)) {
            After after = method.getAnnotation(After.class);
            aspect = new GeneticAspect(after.value(), after.method(), after.anno(), order);
            aspect.setAfter(() -> invokeMethod(aspectObject, method, null));
        } else {
            Around around = method.getAnnotation(Around.class);
            aspect = new GeneticAspect(around.value(), around.method(), around.anno(), order);
            aspect.setAround(((invocation, proxyChain) -> invokeMethod(aspectObject, method, new Object[]{invocation, proxyChain})));
        }
        return aspect;
    }

    private static Object invokeMethod(Object obj, Method method, Object[] args) {
        try {
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
