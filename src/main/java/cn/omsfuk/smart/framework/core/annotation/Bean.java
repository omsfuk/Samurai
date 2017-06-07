package cn.omsfuk.smart.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-26.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Bean {
    String value() default "";
    BeanScope scope() default BeanScope.singleton;
}
