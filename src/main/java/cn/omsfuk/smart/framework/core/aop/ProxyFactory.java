package cn.omsfuk.smart.framework.core.aop;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.List;

/**
 * Created by omsfuk on 17-6-4.
 */
public class ProxyFactory {

    public static Class<?> weaveAspect(Class<?> cls, List<GeneticAspect> aspects) {
        ProxyChain proxyChain = getProxyChain(aspects);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallbackType(MethodInterceptor.class);
        Class<?> proxyClass = enhancer.createClass();

        ProxyChain last = proxyChain;
        while (last.getNext() != null) {
            last = last.getNext();
        }

        final ProxyChain lastProxy = last;
        Enhancer.registerStaticCallbacks(proxyClass, new MethodInterceptor[] {
                (object, method, args, proxyMethod) -> {
                    GeneticAspect aspect = new GeneticAspect(".+", ".+", null, 0);
                    aspect.setAround(((invocation, proxyChain1) -> invocation.invokeMethod()));
                    ProxyChain lastMethodInvocation = new ProxyChain(aspect, null);
                    lastProxy.setNext(lastMethodInvocation);
                    if (method.getDeclaringClass() == cls) {
                        Invocation invocation = new Invocation(object, method, proxyMethod, args);
                        return proxyChain.doProxyChain(invocation);
                    } else {
                        return proxyMethod.invokeSuper(object, args);
                    }
                }
        });
        return proxyClass;
    }

    private static ProxyChain getProxyChain(List<GeneticAspect> aspects) {
        ProxyChain proxyChain = null;
        for (int i = aspects.size() - 1; i >=0; i--) {
            proxyChain = new ProxyChain(aspects.get(i), proxyChain);
        }
        return proxyChain;
    }

}
