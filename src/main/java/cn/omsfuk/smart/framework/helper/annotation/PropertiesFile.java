package cn.omsfuk.smart.framework.helper.annotation;

import java.lang.annotation.*;

/**
 * Properties文件路径
 * Created by omsfuk on 17-5-26.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertiesFile {
    String value() default "smart.properties";
}
