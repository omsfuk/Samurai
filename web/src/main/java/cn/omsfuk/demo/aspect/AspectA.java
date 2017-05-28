package cn.omsfuk.demo.aspect;

import cn.omsfuk.smart.framework.aop.annotation.Around;
import cn.omsfuk.smart.framework.aop.annotation.Aspect;
import cn.omsfuk.smart.framework.aop.annotation.Before;
import cn.omsfuk.smart.framework.aop.annotation.Order;

/**
 * Created by omsfuk on 17-5-27.
 */

@Aspect
@Order(1)
public class AspectA {

    @Before("cn\\.omsfuk\\.demo\\.controller.+")
    public void test() {
        System.out.println("Aspect A test");
    }

    @Before("cn\\.omsfuk\\.demo\\.controller.+")
    public void test2() {
        System.out.println("Aspect A test2");
    }

    @Before("cn\\.omsfuk\\.demo\\.controller.+")
    public void test3() {
        System.out.println("Aspect A test3");
    }
}
