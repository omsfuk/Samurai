package cn.omsfuk.smart.framework.helper.test;

import cn.omsfuk.smart.framework.helper.PropertyHelper;
import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
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
        PropertyHelper.attachPropertyFileWithObject(this);
        System.out.println(version);
    }

    public static void main(String[] args) {
        new HelperTest().test();
    }
}
