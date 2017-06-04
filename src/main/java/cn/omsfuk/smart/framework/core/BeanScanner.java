package cn.omsfuk.smart.framework.core;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by omsfuk on 17-6-4.
 */
public interface BeanScanner {

    List<Class<?>> scanBeanByAnnotation(List<Class<? extends Annotation>> annos, String... packages);

    List<Class<?>> scanBeanByAnnotation(Class<? extends Annotation> annotation, String... packages);

}
