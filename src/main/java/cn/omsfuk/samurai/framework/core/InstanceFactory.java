package cn.omsfuk.samurai.framework.core;

import cn.omsfuk.samurai.framework.util.ClassUtil;
import cn.omsfuk.samurai.framework.core.annotation.Inject;
import cn.omsfuk.samurai.framework.core.bean.BeanConstructor;
import cn.omsfuk.samurai.framework.core.bean.BeanContext;

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

    public Object getInstanceWithoutAutowiring(Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object autowireField(Object bean) {
        Stream.of(ClassUtil.getOriginClass(bean.getClass()).getDeclaredFields())
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
