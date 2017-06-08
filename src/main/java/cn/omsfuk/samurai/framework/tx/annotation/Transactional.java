package cn.omsfuk.samurai.framework.tx.annotation;

import java.lang.annotation.*;

/**
 * Created by omsfuk on 17-5-31.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Transactional {
    Propagation propagation() default Propagation.REQUIRED;
}
