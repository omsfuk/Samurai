package cn.omsfuk.smart.framework.helper;

import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-26.
 */
public final class PropertyHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyHelper.class);

    /**
     * 将类的属性和Properties中的key关联起来
     * @param obj
     */
    public static void attachPropertyFileWithObject(Object obj) {
        attachPropertyFile(obj, false);
    }

    public static void attachPropertyFileWithClass(Class<?> cls) {
        attachPropertyFile(cls, true);
    }

    private static void attachPropertyFile(Object obj, boolean isStatic) {
        Class<?> cls = null;
        if(isStatic) {
            cls = (Class<?>) obj;
        }
        if(!cls.isAnnotationPresent(PropertiesFile.class)) {
            return ;
        }
        String filePath = cls.getAnnotation(PropertiesFile.class).value();
        Properties properties = new Properties();
        try {
            properties.load(PropertyHelper.class.getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e) {
            LOGGER.error("fail to read properties file : {}", filePath);
            throw new RuntimeException(e);
        }

        Stream.of(cls.getDeclaredFields()).forEach(field -> {
            if(Modifier.isStatic(field.getModifiers()) == isStatic && field.isAnnotationPresent(Property.class)) {
                field.setAccessible(true);
                try {
                    String value = properties.getProperty(field.getAnnotation(Property.class).value());
                    if(value != null) {
                        /**
                         * 如果是静态属性，则obj可以不填，所以这段代码是通用的
                         */
                        field.set(obj, value);
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("illegalAccess");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获得属性
     * @param filePath
     * @param propertyName
     * @return
     * @throws IOException
     */
    public static String getProperty(String filePath, String propertyName)  {
        Properties properties = new Properties();
        try {
            properties.load(PropertyHelper.class.getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e) {
            LOGGER.error("can not found property named {}", propertyName);
            throw new RuntimeException(e);
        }
        return properties.getProperty(propertyName);
    }

}
