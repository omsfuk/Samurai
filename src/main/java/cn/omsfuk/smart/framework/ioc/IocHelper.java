package cn.omsfuk.smart.framework.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by omsfuk on 17-5-26.
 */
public final class IocHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(IocHelper.class);

    /**
     * 自动满足依赖，并获得实例
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T autoInstance(Class<T> cls) {
        T obj = BeanHelper.satisfyConstructorDependency(cls);
        BeanHelper.satisfyFieldDependency(obj);
        return obj;
    }
}
