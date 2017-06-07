package cn.omsfuk.smart.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-30.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Repository {
    String value() default "";
    BeanScope scope() default BeanScope.singleton;
}
