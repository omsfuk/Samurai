package cn.omsfuk.smart.framework.aop.annotation;

/**
 * Created by omsfuk on 17-5-28.
 */
@FunctionalInterface
public interface RdConsumer<T, U, V> {

    Object apply(T t, U u, V v);

}
