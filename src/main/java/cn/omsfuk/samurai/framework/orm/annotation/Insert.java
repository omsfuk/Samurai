package cn.omsfuk.samurai.framework.orm.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-30.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Insert {
    String value();
}
