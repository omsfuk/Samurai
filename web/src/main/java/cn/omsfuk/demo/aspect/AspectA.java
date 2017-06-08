package cn.omsfuk.demo.aspect;

import cn.omsfuk.samurai.framework.core.annotation.Aspect;
import cn.omsfuk.samurai.framework.core.annotation.Before;
import cn.omsfuk.samurai.framework.core.annotation.Order;

/**
 * Created by omsfuk on 17-5-27.
 */

@Aspect
@Order(10)
public class AspectA {

    @Before("cn\\.omsfuk\\.demo\\.controller.+")
    public void test1() {
        System.out.println("Aspect A test1");
    }

    @Before("cn\\.omsfuk\\.demo\\.controller.+")
    public void test2() {
        System.out.println("Aspect A test2");
    }

}
