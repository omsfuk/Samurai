package cn.omsfuk.smart.framework.helper;

import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-26.
 */

@PropertiesFile
public class ClassHelper {

    private static final ClassLoader classLoader = ClassHelper.class.getClassLoader();

    static {
        PropertyHelper.attachPropertyFileWithClass(ClassHelper.class);
    }

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    public static List<Class<?>> loadClassByAnnotation(Class<? extends Annotation> annotation, String... packages) {
        return loadClassByPackage(packages).stream()
                .filter(cls -> cls.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public static List<Class<?>> loadClassByAnnotation(List<Class<? extends Annotation>> annotations, String... packages) {
        return loadClassByPackage(packages).stream()
                .filter(cls -> annotations.stream().anyMatch(annotation -> cls.isAnnotationPresent(annotation)))
                .collect(Collectors.toList());
    }

    public static List<Class<?>> loadClassByPackage(String... packages) {
        List<Class<?>> list = new LinkedList<>();
        Stream.of(packages).forEach(packageName -> {
            URL url = getClassLoader().getResource(packageName.replace(".", "/"));
            if (!url.getProtocol().equals("jar")) {
                list.addAll(loadClassFromNormalFile(new File(url.getPath()), packageName));
            } else {
                list.addAll(loadClassFromJarFile(url));
            }
        });
        return list;
    }

    public static Class<?> loadClass(String name) {
        try {
            return getClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initClass(Class<?> cls) {
        try {
            Class.forName(cls.getName());
        } catch (ClassNotFoundException e) {
            new RuntimeException(e);
        }
    }

    private static List<Class<?>> loadClassFromNormalFile(File filePath, String packageName) {
        List<Class<?>> list = new LinkedList<>();
        Stream.of(filePath.listFiles((file) -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory())).forEach(file -> {
            if (file.isDirectory()) {
                list.addAll(loadClassFromNormalFile(file, packageName + "." + file.getName()));
            } else {
                list.add(loadClass(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        });
        return list;
    }

    private static List<Class<?>> loadClassFromJarFile(URL url) {
        List<Class<?>> list = new LinkedList<>();
        try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            JarFile jarFile = connection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                String entryName = jarEntries.nextElement().getName();
                if (entryName.endsWith(".class")) {
                    list.add(loadClass(entryName.substring(0, entryName.length() - 6).replace("/", ".")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Class<?> getOriginClass(Class<?> cls) {
        int pos = cls.getName().indexOf("$$");
        if(pos != -1) {
            try {
                return Class.forName(cls.getName().substring(0, pos));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return cls;
        }
    }
}
