package cn.omsfuk.smart.framework.ioc.exception;

/**
 * Created by omsfuk on 17-5-26.
 */
public class BeanNotFoundException extends RuntimeException {

    private String message = "Bean not found : ";

    public BeanNotFoundException(String beanName) {
        this.message += beanName;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
