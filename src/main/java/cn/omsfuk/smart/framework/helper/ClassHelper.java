package cn.omsfuk.smart.framework.helper;

import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Created by omsfuk on 17-5-26.
 */

@PropertiesFile
public class ClassHelper {

    @Property("component.scan.path")
    private static String PACKAGE_PATH;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassHelper.class);

    private static final ClassLoader classLoader = ClassHelper.class.getClassLoader();

    static {
        PropertyHelper.attachPropertyFileWithClass(ClassHelper.class);
    }

    public static List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return getClassesByAnnotation(PACKAGE_PATH, annotation);
    }

    public static List<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        URL url = classLoader.getResource(packageName.replace('.', '/'));
        return loadClass(new File(url.getPath()), packageName).stream().filter(cls -> cls.isAnnotationPresent(annotation)).collect(Collectors.toList());
    }

    public static List<Class<?>> getClassesByAnnotation(String packageName, List<Class<? extends Annotation>> annotations) {
        URL url = ClassHelper.class.getClassLoader().getResource(packageName.replace('.', '/'));
        return loadClass(new File(url.getPath()), packageName).stream()
                .filter(cls -> annotations.stream().anyMatch(annotation -> cls.isAnnotationPresent(annotation)))
                .collect(Collectors.toList());
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

    public static void initClass(Class<?> cls) {
        try {
            Class.forName(cls.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.toString());
            new RuntimeException(e);
        }
    }

    // TODO 此类重构，尤其是jar加载

    private static List<Class<?>> loadClass(File filePath, String packageName) {
        List<Class<?>> classes = new LinkedList<>();
//        File[] files = filePath.listFiles();
//        System.out.println(filePath);
//        Stream.of(files).forEach(file -> {
//            if(file.isDirectory()) {
//                classes.addAll(loadClass(file, packageName + "." + file.getName()));
//            } else {
//                if(file.getName().endsWith(".class")) {
//                    try {
//                        classes.add(classLoader.loadClass(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
//                    } catch (ClassNotFoundException e) {
//                        LOGGER.error("class not found : {}", file.getAbsoluteFile());
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        });


        try {
            // 从包名获取 URL 类型的资源
            Enumeration<URL> urls = classLoader.getResources(packageName.replace(".", "/"));
            // 遍历 URL 资源
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    // 获取协议名（分为 file 与 jar）
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        // 若在 class 目录中，则执行添加类操作
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classes, packagePath, packageName);
                    } else if (protocol.equals("jar")) {
                        // 若在 jar 包中，则解析 jar 包中的 entry
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            // 判断该 entry 是否为 class
                            if (jarEntryName.endsWith(".class")) {
                                // 获取类名
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                // 执行添加类操作
                                doAddClass(classes, className);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取类出错！", e);
        }


        return classes;
    }

    private static void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            // 获取包名路径下的 class 文件或目录
            File[] files = new File(packagePath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
                }
            });
            // 遍历文件或目录
            for (File file : files) {
                String fileName = file.getName();
                // 判断是否为文件或目录
                if (file.isFile()) {
                    // 获取类名
                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    if (StringUtils.isNotEmpty(packageName)) {
                        className = packageName + "." + className;
                    }
                    // 执行添加类操作
                    doAddClass(classList, className);
                } else {
                    // 获取子包
                    String subPackagePath = fileName;
                    if (StringUtils.isNotEmpty(packagePath)) {
                        subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    // 子包名
                    String subPackageName = fileName;
                    if (StringUtils.isNotEmpty(packageName)) {
                        subPackageName = packageName + "." + subPackageName;
                    }
                    // 递归调用
                    addClass(classList, subPackagePath, subPackageName);
                }
            }
        } catch (Exception e) {
            LOGGER.error("添加类出错！", e);
        }
    }

    private static void doAddClass(List<Class<?>> classList, String className) {
        try {
            classList.add(classLoader.loadClass(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getOriginClass(Class<?> cls) {
        int pos = cls.getName().indexOf("$$");
        if(pos != -1) {
            try {
                return Class.forName(cls.getName().substring(0, pos));
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.toString());
                throw new RuntimeException(e);
            }
        } else {
            return cls;
        }
    }
}
