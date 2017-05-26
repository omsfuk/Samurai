package cn.omsfuk.smart.framework.ioc;

import cn.omsfuk.smart.framework.ioc.annotation.Inject;
import cn.omsfuk.smart.framework.ioc.exception.BeanNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-26.
 */
public final class BeanHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanHelper.class);

    private static ThreadLocal<Map<Class<?>, Object>> localBeanMap = new ThreadLocal<>();

    private static Map<Class<?>, Object> getBeanMap() {
        return localBeanMap.get();
    }

    private static void setBeanMap(Map<Class<?>, Object> beanMap) {
        localBeanMap.set(beanMap);
    }

    private static List<Class<?>> singletonBeans = new LinkedList<>();

    private static List<Class<?>> protorypeBeans = new LinkedList<>();

    static {

    }

    /**
     * 自动满足属性依赖
     * @param obj
     */
    public static void satisfyFieldDependency(Object obj) {
        Class<?> cls = obj.getClass();
        Stream.of(cls.getDeclaredFields()).forEach((field -> {
            if(field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    field.set(obj, BeanHelper.getBean(field.getType()));
                } catch (IllegalAccessException e) {
                    LOGGER.error("IllegalAccess", e);
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    /**
     * 自动满足构造器依赖，获得实例
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T satisfyConstructorDependency(Class<T> cls) {
        Constructor<?>[] constructors = cls.getConstructors();

        if(constructors.length != 1) {
            RuntimeException exception = new RuntimeException("constructor number more than 1 or none");
            LOGGER.error("constructor number more than 1 or none", exception);
            throw exception;
        }

        Constructor<?> constructor = constructors[0];
        Object[] paramInstances = new Object[constructor.getParameterCount()];
        Class<?>[] paramClasses = constructor.getParameterTypes();
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            paramInstances[i] = BeanHelper.getBean(paramClasses[i]);
        }
        try {
            return (T) constructor.newInstance(paramInstances);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            RuntimeException exception = new RuntimeException("constructor param bean can not satisfy");
            LOGGER.error("constructor param bean can not satisfy", exception);
            throw exception;
        }
    }

    /**
     * 获得bean
     * @param cls
     * @return
     */
    public static Object getBean(Class<?> cls) {
        Map<Class<?>, Object> beanMap = getBeanMap();
        if(beanMap == null) {
            LOGGER.error("bean not found : {}", cls.getName());
            throw new BeanNotFoundException(cls.getName());
        }
        if(!beanMap.containsKey(cls)) {
            LOGGER.error("bean not found : {}", cls.getName());
            throw new BeanNotFoundException(cls.getName());
        }
        LOGGER.debug("return bean from bean container : {}", cls);
        return beanMap.get(cls);
    }

    public static LinkedList<Object> getBeanByAnnotation(Class<? extends Annotation> cls) {
        LinkedList<Object> result = new LinkedList<>();
        getBeanMap().forEach((key, value) -> {
            if(key.isAnnotationPresent(cls)) {
                result.add(value);
            }
        });
        return result;
    }

    /**
     * 添加bean
     * @param object
     */
    public static void setBean(Object object) {
        Map<Class<?>, Object> beanMap = getBeanMap();
        if(beanMap == null) {
            beanMap = new HashMap<>();
            setBeanMap(beanMap);
        }
        beanMap.put(object.getClass(), object);
        LOGGER.debug("add bean to bean container : {}", object.getClass());
    }
}
