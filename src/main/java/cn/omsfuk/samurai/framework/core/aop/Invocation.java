package cn.omsfuk.samurai.framework.core.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法调用的信息。包括方法名，调用目标对象，方法，代理后的方法以及参数
 * Created by omsfuk on 17-6-4.
 */
public class Invocation {

    /**
     * 代理目标对象
     */
    private Object target;

    /**
     * 被代理的方法
     */
    private Method method;

    /**
     * 方法代理
     */
    private MethodProxy methodProxy;

    /**
     * 参数
     */
    private Object[] args;

    public Invocation(Object target, Method method, MethodProxy methodProxy, Object[] args) {
        this.target = target;
        this.method = method;
        this.methodProxy = methodProxy;
        this.args = args;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public MethodProxy getMethodProxy() {
        return methodProxy;
    }

    public Object[] getArgs() {
        return args;
    }

    /**
     * 调用原方法
     * @return
     */
    public Object invokeMethod() {
        try {
            return methodProxy.invokeSuper(target, args);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
