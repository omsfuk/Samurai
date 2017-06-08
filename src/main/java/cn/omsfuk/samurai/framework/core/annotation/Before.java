package cn.omsfuk.samurai.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-27.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {
    String value();
    String method() default ".+";
    Class<? extends Annotation> anno() default Annotation.class;
}
