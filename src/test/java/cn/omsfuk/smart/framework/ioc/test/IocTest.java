package cn.omsfuk.smart.framework.ioc.test;

import cn.omsfuk.smart.framework.ioc.BeanContext;
import cn.omsfuk.smart.framework.ioc.BeanHelper;
import cn.omsfuk.smart.framework.ioc.DefaultBeanContext;
import cn.omsfuk.smart.framework.ioc.IocHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by omsfuk on 17-5-26.
 */


public class IocTest {

    public static class BeanA {

        private String name;

        public BeanA(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void test() {
        BeanHelper.setBean("ioc test");
        BeanA beanA = IocHelper.autoInstance(BeanA.class);
        Assert.assertEquals("ioc test", beanA.getName());
    }

    @Test
    public void test2() {
        Properties properties = new Properties();
        try {
            properties.load(IocTest.class.getClassLoader().getResourceAsStream("smart.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(properties.getProperty("smart"));
    }
}
