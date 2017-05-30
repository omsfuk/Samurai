package cn.omsfuk.smart.framework.ioc.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-30.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Component {
    String value() default "";
}
