package cn.omsfuk.samurai.framework.core.aop;

/**
 * 代理链。一个ProxyChain对应一层Aspect。
 * Created by omsfuk on 17-6-5.
 */
public class ProxyChain {

    /**
     * 切面
     */
    private GeneticAspect aspect;

    /**
     * 下一个代理
     */
    private ProxyChain proxyChain;

    public ProxyChain(GeneticAspect aspect, ProxyChain proxyChain) {
        this.aspect = aspect;
        this.proxyChain = proxyChain;
    }

    /**
     * 继续执行代理链
     * @param invocation
     * @return
     */
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
