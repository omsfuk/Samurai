package cn.omsfuk.samurai.framework.core.bean;

import cn.omsfuk.samurai.framework.core.InstanceFactory;
import cn.omsfuk.samurai.framework.core.exception.BeanConflictException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例域的BeanFactory
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
