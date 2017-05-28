package cn.omsfuk.demo.aspect;

import cn.omsfuk.smart.framework.aop.ProxyChain;
import cn.omsfuk.smart.framework.aop.annotation.After;
import cn.omsfuk.smart.framework.aop.annotation.Around;
import cn.omsfuk.smart.framework.aop.annotation.Aspect;
import cn.omsfuk.smart.framework.aop.annotation.Order;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-27.
 */


@Aspect
@Order(10)
public class AspectB {

    @After("cn.omsfuk.demo")
    public void testB() {
    }

    @Around("cn\\.omsfuk\\.demo\\.controller.+")
    public Object around(Method method, Object[] args, ProxyChain proxyChain) {
        System.out.println(method.getName());
        System.out.println(method.getParameterCount());
        Stream.of(method.getParameterTypes()).forEachOrdered(System.out::println);
        return proxyChain.doProxyChain(method, args);
    }
}
