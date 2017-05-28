package cn.omsfuk.smart.framework.helper;

import cn.omsfuk.smart.framework.aop.ProxyChain;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.List;

/**
 * Created by omsfuk on 17-5-27.
 */
public final class CgLibUtil {
    public static <T> T getProxy(Class<?> cls, List<ProxyChain> proxyChainList) {
        int proxyCount = proxyChainList.size();
        System.out.println("proxy count : " + proxyCount);

        for (int i = 0; i < proxyCount; i++) {
            if(i != proxyCount - 1) {
                proxyChainList.get(i).setProxyChain(proxyChainList.get(i + 1));
            }
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        ProxyChain finalStartProxy = proxyCount > 0 ? proxyChainList.get(0) : null;
        ProxyChain finalLastProxy = proxyCount > 0 ? proxyChainList.get(proxyCount - 1) : null;

        enhancer.setCallback((MethodInterceptor) (object, method, args, proxyMethod) -> {
            System.out.println("Object Name " + object.getClass() + " " + "Method Name " + method.getName());
            ProxyChain invokeMethodProxy = new ProxyChain(object.getClass().getName(), method.getName());
            invokeMethodProxy.setAround((method0, args0, proxyChain) -> {
                try {
                    return proxyMethod.invokeSuper(object, args0);
                } catch (Throwable throwable) {
                    // TODO 异常处理
                    throwable.printStackTrace();
                }
                return null;
            });

            if(finalStartProxy == null) {
                return invokeMethodProxy.doProxyChain(null, null);
            }

            finalLastProxy.setProxyChain(invokeMethodProxy);
            return finalStartProxy.doProxyChain(method, args);
        });
        return (T) enhancer.create();
    }
}
