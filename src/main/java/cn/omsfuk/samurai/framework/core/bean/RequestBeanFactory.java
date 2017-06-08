package cn.omsfuk.samurai.framework.core.bean;

import cn.omsfuk.samurai.framework.core.InstanceFactory;

/**
 * 请求域的BeanFactory
 * Created by omsfuk on 17-6-4.
 */
public class RequestBeanFactory extends SingletonBeanFactory {

    public RequestBeanFactory(InstanceFactory instanceFactory) {
        super(instanceFactory);
    }
}
