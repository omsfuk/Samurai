package cn.omsfuk.demo;

import cn.omsfuk.demo.controller.MainController;
import cn.omsfuk.smart.framework.ioc.BeanContext;
import cn.omsfuk.smart.framework.ioc.DefaultBeanContext;

/**
 * Created by omsfuk on 17-5-30.
 */
public class Main {

    public static void main(String[] args) {
        BeanContext beanContext = new DefaultBeanContext("cn.omsfuk.demo");
        ((MainController) beanContext.getBean("MainController")).test();
    }
}
