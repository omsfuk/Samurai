package cn.omsfuk.smart.framework.core;

/**
 * Created by omsfuk on 17-6-4.
 */
public class BeanContextManager {

    private static ThreadLocal<BeanContext> beanContextThreadLocal = new ThreadLocal<>();

    public static BeanContext get() {
        return beanContextThreadLocal.get();
    }

    public static void set(BeanContext beanContext) {
        beanContextThreadLocal.set(beanContext);
    }

    public static void remove() {
        beanContextThreadLocal.remove();
    }

}
