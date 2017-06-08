package cn.omsfuk.samurai.framework.util.test;

import cn.omsfuk.samurai.framework.util.PropertyUtil;
import cn.omsfuk.samurai.framework.util.annotation.PropertiesFile;
import cn.omsfuk.samurai.framework.util.annotation.Property;
import org.junit.Test;

/**
 * Created by omsfuk on 17-5-26.
 */

@PropertiesFile
public class HelperTest {

    @Property("smart")
    private String version = "ver1.0.0";

    @Test
    public void test() {
        PropertyUtil.attachPropertyFileWithObject(this);
        System.out.println(version);
    }

    public static void main(String[] args) {
        new HelperTest().test();
    }
}
