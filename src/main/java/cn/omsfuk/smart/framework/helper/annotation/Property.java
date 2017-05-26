package cn.omsfuk.smart.framework.helper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对应Properties文件属性名称
 * Created by omsfuk on 17-5-26.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {
    String value();
}
