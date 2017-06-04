package cn.omsfuk.demo;

import cn.omsfuk.demo.controller.MainController;
import cn.omsfuk.smart.framework.core.BeanContext;
import cn.omsfuk.smart.framework.core.impl.DefaultBeanContext;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by omsfuk on 17-5-30.
 */
public class Main {

    public Main() {
        try {
            Enumeration<URL> urls = this.getClass().getClassLoader().getResources("cn/omsfuk");
            while(urls.hasMoreElements()) {
                URL url = urls.nextElement();
                System.out.println(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main();
//        BeanContext beanContext = new DefaultBeanContext("cn.omsfuk.demo");
//        ((MainController) beanContext.getBean("MainController")).test();
    }
}
