package cn.omsfuk.samurai.framework.ioc.test;

import org.junit.Test;

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
    public void test2() {
        Properties properties = new Properties();
        try {
            properties.load(IocTest.class.getClassLoader().getResourceAsStream("samurai.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(properties.getProperty("smart"));
    }
}
