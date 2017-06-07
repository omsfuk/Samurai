package cn.omsfuk.smart.framework.core.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by omsfuk on 17-6-4.
 */
public class Invocation {

    private Object target;

    private Method method;

    private MethodProxy methodProxy;

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

    public Object invokeMethod() {
        try {
            return methodProxy.invokeSuper(target, args);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
