package cn.omsfuk.smart.framework.core;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * Created by omsfuk on 17-6-2.
 */
public class ConstructorMethod {

    public Supplier<Object> constructor;

    public ConstructorMethod(Supplier<Object> constructor) {
        this.constructor = constructor;
    }

    public Object constructObject() {
        return constructor.get();
    }
}
