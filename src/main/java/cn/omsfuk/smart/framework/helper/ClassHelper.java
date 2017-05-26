package cn.omsfuk.smart.framework.helper;

import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-26.
 */
public class ClassHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassHelper.class);

    private static final ClassLoader classLoader = ClassHelper.class.getClassLoader();

    public static List<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        URL url = ClassHelper.class.getClassLoader().getResource(packageName.replace('.', '/'));
        return loadClass(new File(url.getPath()), packageName).stream().filter(cls -> cls.isAnnotationPresent(annotation)).collect(Collectors.toList());
    }

    public static List<Class<?>> getClasses(String packageName) {
        URL url = ClassHelper.class.getClassLoader().getResource(packageName.replace('.', '/'));
        return loadClass(new File(url.getPath()), packageName);
    }

    public static Class<?> loadClass(String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            LOGGER.error("class not found : {}", name);
            throw new RuntimeException(e);
        }
    }

    private static List<Class<?>> loadClass(File filePath, String packageName) {
        List<Class<?>> classes = new LinkedList<>();
        File[] files = filePath.listFiles();
        Stream.of(files).forEach(file -> {
            if(file.isDirectory()) {
                classes.addAll(loadClass(file, packageName + "." + file.getName()));
            } else {
                if(file.getName().endsWith(".class")) {
                    try {
                        classes.add(classLoader.loadClass(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error("class not found : {}", file.getAbsoluteFile());
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return classes;
    }
}
