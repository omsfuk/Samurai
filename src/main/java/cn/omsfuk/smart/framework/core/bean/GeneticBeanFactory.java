package cn.omsfuk.smart.framework.core.bean;

import cn.omsfuk.smart.framework.core.AbstractBean;
import cn.omsfuk.smart.framework.core.InstanceFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Created by omsfuk on 17-6-4.
 */
public abstract class GeneticBeanFactory {

    private InstanceFactory instanceFactory;

    public abstract Object getBean(String name);

    public abstract Map<String, AbstractBean> getBeans();

    public InstanceFactory getInstanceBeanFactory() {
        return instanceFactory;
    };

    public GeneticBeanFactory(InstanceFactory instanceFactory) {
        this.instanceFactory = instanceFactory;
    }

    public Object getBean(Class<?> beanClass) {
        Optional<AbstractBean> beanOptional = getBeans().values().stream().filter(bean0 -> beanClass.isAssignableFrom(bean0.getBeanType())).findAny();
        if (beanOptional.isPresent()) {
            AbstractBean bean = beanOptional.get();
            if (bean.getInstance() == null) {
                bean.setInstance(getInstanceBeanFactory().getInstance(bean.getConstructor()));
            }
            return bean.getInstance();
        } else {
            return null;
        }
    }
}
