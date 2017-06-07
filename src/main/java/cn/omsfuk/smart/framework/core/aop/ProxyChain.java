package cn.omsfuk.smart.framework.core.aop;

/**
 * Created by omsfuk on 17-6-5.
 */
public class ProxyChain {

    private GeneticAspect aspect;

    private ProxyChain proxyChain;

    public ProxyChain(GeneticAspect aspect, ProxyChain proxyChain) {
        this.aspect = aspect;
        this.proxyChain = proxyChain;
    }

    public Object doProxyChain(Invocation invocation) {
        Object result = null;
        if (aspect.getBefore() != null) {
            aspect.getBefore().run();
        }
        if (aspect.getAround() != null) {
            result = aspect.getAround().apply(invocation, proxyChain);
        } else {
            result = proxyChain.doProxyChain(invocation);
        }
        if (aspect.getAfter() != null) {
            aspect.getAfter().run();
        }
        return result;
    }

    public ProxyChain getNext() {
        return proxyChain;
    }

    public void setNext(ProxyChain proxyChain) {
        this.proxyChain = proxyChain;
    }
}
