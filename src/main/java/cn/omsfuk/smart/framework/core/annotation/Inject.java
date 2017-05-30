package cn.omsfuk.smart.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-26.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Inject {

    String value() default "";
}
