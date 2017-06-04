package cn.omsfuk.smart.framework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by omsfuk on 17-5-27.
 */
public class AnnotationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationHelper.class);

    public static boolean isAnnotationPresent(Class<?> cls, Class<? extends Annotation> annotation) {
        Class<?> originClass = getOriginClass(cls);
        return originClass.isAnnotationPresent(annotation);
    }

    public static boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotation) {
        Method originMethod = null;
        try {
            originMethod = getOriginMethod(method);
            return originMethod.isAnnotationPresent(annotation);
        } catch (NoSuchMethodException e) {
//            LOGGER.error(e.getMessage());
            // TODO 不抛出异常，应该不会出现找不到应该有的方法的情况吧
//            throw new RuntimeException(e);
            return false;
        }
    }

    public static <T> Annotation getAnnotation(Method method, Class<T> annotation) {
        try {
            Method originMethod = getOriginMethod(method);
            return originMethod.getAnnotation((Class<Annotation>) annotation);
        } catch (NoSuchMethodException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Method getOriginMethod(Method method) throws NoSuchMethodException {
        Class<?> originClass = getOriginClass(method.getDeclaringClass());
        Method originMethod = null;
        return originClass.getMethod(method.getName(), method.getParameterTypes());
    }

    public static Annotation[] getAnnotations(Class<?> cls) {
        Class<?> originClass = getOriginClass(cls);
        return cls.getAnnotations();
    }

    public static Annotation[] getAnnotations(Method method) {
        Method originMethod = null;
        try {
            originMethod = getOriginMethod(method);
            return originMethod.getAnnotations();
        } catch (NoSuchMethodException e) {
            LOGGER.error(e.getMessage());
             throw new RuntimeException(e);
        }
    }

    public static Class<?> getOriginClass(Class<?> cls) {
        Class<?> originClass = cls;
        int pos = cls.getName().indexOf("$$");
        if(pos != -1) {
            originClass = ClassHelper.loadClass(cls.getName().substring(0, pos));
        }
        return originClass;
    }
}
