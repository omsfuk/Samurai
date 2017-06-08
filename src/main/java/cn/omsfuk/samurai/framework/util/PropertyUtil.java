package cn.omsfuk.samurai.framework.util;

import cn.omsfuk.samurai.framework.util.annotation.PropertiesFile;
import cn.omsfuk.samurai.framework.util.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-26.
 */
public final class PropertyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    private static final Pattern pattern = Pattern.compile("(.+?)=(.+)");

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
            properties.load(PropertyUtil.class.getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e) {
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
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 获得属性
     * @param filePath
     * @param propertyName
     * @return
     */
    public static String getProperty(String filePath, String propertyName)  {
        Properties properties = new Properties();
        try {
            properties.load(PropertyUtil.class.getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty(propertyName);
    }

    public static Map<String, String> listAllProperties(String filePath) {
        Map<String, String> result = new HashMap<>();
        Properties properties = new Properties();
        BufferedReader reader = new BufferedReader(new InputStreamReader(PropertyUtil.class.getClassLoader().getResourceAsStream(filePath)));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    result.put(matcher.group(1), matcher.group(2));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        listAllProperties("samurai.properties").forEach((key, value) -> {
            System.out.println(value);
        });
    }

}
