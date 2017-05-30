package cn.omsfuk.smart.framework.ioc;

import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.helper.PropertyHelper;
import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import cn.omsfuk.smart.framework.ioc.annotation.Bean;
import cn.omsfuk.smart.framework.ioc.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.LinkedList;

/**
 * Created by omsfuk on 17-5-26.
 */

@PropertiesFile
public final class IocHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(IocHelper.class);

    @Property("component.scan.path")
    private static String scanPath;

    static {
        PropertyHelper.attachPropertyFileWithClass(IocHelper.class);
        LOGGER.debug("register beans...");
        LinkedList<Class<? extends Annotation>> annotations = new LinkedList<Class<? extends Annotation>>();
        annotations.add(Bean.class);
        annotations.add(Controller.class);
        annotations.forEach(annotation -> {
            ClassHelper.getClassesByAnnotation(scanPath, annotation).stream().forEach(cls -> {
                Object bean = BeanHelper.satisfyConstructorDependency(cls);
                BeanHelper.setBean(bean);
                LOGGER.debug("register bean {}", bean.getClass().getName());
            });
        });
        LOGGER.debug("register beans complete");
    }

    /**
     * 自动满足依赖，并获得实例
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T autoInstance(Class<T> cls) {
        T obj = BeanHelper.satisfyConstructorDependency(cls);
        return (T)BeanHelper.satisfyFieldDependency(obj);
    }
}
