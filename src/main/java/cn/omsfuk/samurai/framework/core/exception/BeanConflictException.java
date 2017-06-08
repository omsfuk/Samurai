package cn.omsfuk.samurai.framework.core.exception;

/**
 * Created by omsfuk on 17-5-29.
 */
public class BeanConflictException extends RuntimeException {

    public BeanConflictException() {
        super("Bean has already exist");
    }

    public BeanConflictException(String message) {
        super(message);
    }

}
