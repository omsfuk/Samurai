package cn.omsfuk.samurai.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-26.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Controller {
    String value() default "";
    BeanScope scope() default BeanScope.singleton;
}
