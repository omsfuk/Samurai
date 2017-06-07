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
public class PrototypeBeanFactory extends GeneticBeanFactory {

    private Map<String, AbstractBean> beans = new ConcurrentHashMap<>();

    public PrototypeBeanFactory(InstanceFactory instanceFactory) {
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
        return getInstanceBeanFactory().getInstance(beans.get(name).getConstructor());
    }

    @Override
    public Map<String, AbstractBean> getBeans() {
        return beans;
    }
}
