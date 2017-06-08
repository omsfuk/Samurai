package cn.omsfuk.samurai.framework.core.bean;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by omsfuk on 17-6-4.
 */
public interface BeanScanner {

    List<AbstractBean> scanBeanByAnnotation(List<Class<? extends Annotation>> annos, String... packages);

    List<AbstractBean> scanBeanByAnnotation(Class<? extends Annotation> annotation, String... packages);

    List<AbstractBean> scanBean(String... packages);

    List<Class<?>> scanAspect(String... packages);

}
