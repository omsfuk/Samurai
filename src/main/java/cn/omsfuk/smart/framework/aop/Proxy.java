package cn.omsfuk.smart.framework.aop;

/**
 * Created by omsfuk on 17-5-28.
 */
public class Proxy {

    private String packageName;

    private String method;

    private Runnable runnable;

    public Proxy(String packageName, String method, Runnable runnable) {
        this.method = method;
        this.packageName = packageName;
        this.runnable = runnable;
    }

    public void doProxy() {
        runnable.run();
    }

}
