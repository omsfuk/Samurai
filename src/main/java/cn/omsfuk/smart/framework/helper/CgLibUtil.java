package cn.omsfuk.smart.framework.helper;

import cn.omsfuk.smart.framework.core.ProxyChain;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.List;

/**
 * Created by omsfuk on 17-5-27.
 */
public final class CgLibUtil {

    public static Class<?> getProxy(Class<?> cls, List<ProxyChain> proxyChainList) {
        int proxyCount = proxyChainList.size();
        for (int i = 0; i < proxyCount; i++) {
            if(i != proxyCount - 1) {
                proxyChainList.get(i).setProxyChain(proxyChainList.get(i + 1));
            }
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallbackType(MethodInterceptor.class);
        ProxyChain finalStartProxy = proxyCount > 0 ? proxyChainList.get(0) : null;
        ProxyChain finalLastProxy = proxyCount > 0 ? proxyChainList.get(proxyCount - 1) : null;

        Class<?> proxyClass = enhancer.createClass();

        Enhancer.registerCallbacks(proxyClass, new MethodInterceptor[]{(object, method, args, proxyMethod) -> {
            // 过滤掉Object类的方法调用，不然很烦。。。
            if (method.getDeclaringClass() != Object.class) {
                ProxyChain invokeMethodProxy = new ProxyChain(object.getClass().getName(), method.getName(), null);
                invokeMethodProxy.setAround((method0, args0, proxyChain) -> {
                    try {
                        return proxyMethod.invokeSuper(object, args0);
                    } catch (Throwable throwable) {
                        // TODO 异常处理
                        throw new RuntimeException(throwable);
                    }
                });

                if (finalStartProxy == null) {
                    return invokeMethodProxy.doProxyChain(null, null);
                }

                finalLastProxy.setProxyChain(invokeMethodProxy);
                return finalStartProxy.doProxyChain(method, args);
            } else {
                return proxyMethod.invokeSuper(object, args);
            }
        }});
        return enhancer.createClass();
    }
}
