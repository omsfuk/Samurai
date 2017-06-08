package cn.omsfuk.samurai.framework.core.exception;

/**
 * Created by omsfuk on 17-6-5.
 */
public class MoreThanOneConstructorException extends RuntimeException {

    public MoreThanOneConstructorException(Throwable throwable) {
        super(throwable);
    }

    public MoreThanOneConstructorException() {
        super();
    }
}
