package cn.omsfuk.smart.framework.core.bean;

import cn.omsfuk.smart.framework.core.AbstractBean;
import cn.omsfuk.smart.framework.core.InstanceFactory;
import cn.omsfuk.smart.framework.core.exception.BeanConflictException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by omsfuk on 17-6-4.
 */
public class SingletonBeanFactory extends GeneticBeanFactory {

    private Map<String, AbstractBean> beans = new ConcurrentHashMap<>();

    public SingletonBeanFactory(InstanceFactory instanceFactory) {
        super(instanceFactory);
    }

    public void setBean(String name, Object instance) {
        if (beans.containsKey(name)) {
            throw new BeanConflictException();
        }
        beans.put(name, new AbstractBean(name, instance));
    }

    public void setBean(String name, Constructor constructor) {
        if (beans.containsKey(name)) {
            throw new BeanConflictException();
        }
        beans.put(name, new AbstractBean(name, constructor));
    }

    public void setBean(String name, Object configObject, Method method) {
        if (beans.containsKey(name)) {
            throw new BeanConflictException();
        }
        beans.put(name, new AbstractBean(name, configObject, method));
    }

    @Override
    public Object getBean(String name) {
        if (!beans.containsKey(name)) {
            return null;
        }
        AbstractBean bean = beans.get(name);
        if (bean.getInstance() == null) {
            bean.setInstance(getInstanceBeanFactory().getInstance(bean.getConstructor()));
        }
        return getInstanceBeanFactory().autowireField(bean.getInstance());
    }

    @Override
    public Map<String, AbstractBean> getBeans() {
        return beans;
    }
}
