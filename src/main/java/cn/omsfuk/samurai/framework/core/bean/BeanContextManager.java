package cn.omsfuk.samurai.framework.core.bean;

/**
 * Bean的管理器。用于全局静态获得BeanContext
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
