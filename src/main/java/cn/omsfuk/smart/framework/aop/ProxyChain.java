package cn.omsfuk.smart.framework.aop;

import cn.omsfuk.smart.framework.aop.annotation.RdConsumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by omsfuk on 17-5-28.
 */
public class ProxyChain {

    private final String className;

    private final Class<? extends Annotation> anno;

    private final String methodName;

    private Runnable before;

    private Runnable after;

    private RdConsumer<Method, Object[], ProxyChain> around;

    private ProxyChain proxyChain;

    public void setProxyChain(ProxyChain proxyChain) {
        this.proxyChain = proxyChain;
    }

    public void setBefore(Runnable before) {
        this.before = before;
    }

    public void setAfter(Runnable after) {
        this.after = after;
    }

    public void setAround(RdConsumer<Method, Object[], ProxyChain> around) {
        this.around = around;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<? extends Annotation> getAnnotation() {
        return anno;
    }

    public ProxyChain(String className, String methodName, Class<? extends Annotation> anno) {
        this.methodName = methodName;
        this.className = className;
        this.anno = anno;
    }

    public Object doProxyChain(Method method, Object[] args) {
        Object result = null;
        if(before != null) {
            before.run();
        }
        if(around != null) {
            result = around.apply(method, args, proxyChain);
        } else {
            result = proxyChain.doProxyChain(method, args);
        }
        if(after != null) {
            after.run();
        }
        return result;
    }

}
