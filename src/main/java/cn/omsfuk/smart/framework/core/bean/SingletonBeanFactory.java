package cn.omsfuk.smart.framework.core.bean;

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

    public void setBean(AbstractBean bean) {
        if (beans.containsKey(bean.getName())) {
            throw new BeanConflictException();
        }
        beans.put(bean.getName(), bean);
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
