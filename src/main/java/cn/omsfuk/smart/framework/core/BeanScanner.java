package cn.omsfuk.smart.framework.core;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by omsfuk on 17-6-4.
 */
public interface BeanScanner {

    /**
     *
     * @param packageName
     * @param annos
     * @return
     */
    List<Class<?>> scanBeanByAnnotation(String packageName, List<Class<? extends Annotation>> annos);

}
