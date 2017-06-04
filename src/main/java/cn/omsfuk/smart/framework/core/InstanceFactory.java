package cn.omsfuk.smart.framework.core;

import cn.omsfuk.smart.framework.core.annotation.Inject;
import cn.omsfuk.smart.framework.helper.ClassHelper;

import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-6-4.
 */
public class InstanceFactory {

    private BeanContext beanContext;

    public InstanceFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    public Object getInstance(BeanConstructor beanConstructor) {
        return autowireField(beanConstructor.construct(getArgsFromBeanContext(beanConstructor.getParamTypes())));
    }

    public Object autowireField(Object bean) {
        Stream.of(ClassHelper.getOriginClass(bean.getClass()).getDeclaredFields())
                        .forEach(field -> {
                            if (field.isAnnotationPresent(Inject.class)) {
                                field.setAccessible(true);
                                try {
                                    field.set(bean, getBeanContext().getBean(field.getType()));
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
        return bean;
    }

    private Object[] getArgsFromBeanContext(Class<?>[] paramTypes) {
        return Stream.of(paramTypes).map(beanContext::getBean).toArray();
    }

    public BeanContext getBeanContext() {
        return beanContext;
    }
}
