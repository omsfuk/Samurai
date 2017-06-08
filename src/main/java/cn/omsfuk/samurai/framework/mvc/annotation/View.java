package cn.omsfuk.samurai.framework.mvc.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-26.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface View {
    String value() default "jsp";
}
