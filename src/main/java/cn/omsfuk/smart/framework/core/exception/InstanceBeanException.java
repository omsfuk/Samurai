package cn.omsfuk.smart.framework.core.exception;

/**
 * Created by omsfuk on 17-5-29.
 */
public class InstanceBeanException extends RuntimeException {

    public InstanceBeanException() {
        super();
    }

    public InstanceBeanException(String messgage) {
        super(messgage);
    }

    public InstanceBeanException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InstanceBeanException(Throwable cause) {
        super(cause);
    }
}
