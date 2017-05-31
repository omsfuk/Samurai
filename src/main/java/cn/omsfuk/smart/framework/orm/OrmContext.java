package cn.omsfuk.smart.framework.orm;

import cn.omsfuk.smart.framework.core.BeanContext;
import cn.omsfuk.smart.framework.core.annotation.BeanScope;
import cn.omsfuk.smart.framework.core.annotation.Repository;
import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.helper.PropertyHelper;
import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import cn.omsfuk.smart.framework.orm.annotation.Delete;
import cn.omsfuk.smart.framework.orm.annotation.Insert;
import cn.omsfuk.smart.framework.orm.annotation.Select;
import cn.omsfuk.smart.framework.orm.annotation.Update;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by omsfuk on 17-5-30.
 */

@PropertiesFile
public class OrmContext {

    @Property("component.scan.path")
    private static String SCAN_PACKAGE;

    @Property("jdbc.url")
    private static String JDBC_URL;

    @Property("jdbc.driver")
    private static String JDBC_DRIVER;

    @Property("jdbc.username")
    private static String JDBC_USERNAME;

    @Property("jdbc.password")
    private static String JDBC_PASSWORD;

    private static QueryRunner queryRunner;

    private static Pattern PARAM_PATTERN = Pattern.compile("#\\{(.+?)}");

    static {
        PropertyHelper.attachPropertyFileWithClass(OrmContext.class);
    }

    private DataSource dataSource;

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initDatasource() {
        BasicDataSource basicDataSource= new BasicDataSource();
        basicDataSource.setUrl(JDBC_URL);
        basicDataSource.setDriverClassName(JDBC_DRIVER);
        basicDataSource.setUsername(JDBC_USERNAME);
        basicDataSource.setPassword(JDBC_PASSWORD);
        dataSource = basicDataSource;
    }

    public OrmContext(BeanContext beanContext) {
        // 初始化数据库连接池
        initDatasource();
        queryRunner = new QueryRunner(dataSource);
        generateProxy(beanContext, getRepositoryInterface());
    }

    private List<Class<?>> getRepositoryInterface() {
        return ClassHelper.getClassesByAnnotation(SCAN_PACKAGE, Repository.class);
    }

    private void generateProxy(BeanContext beanContext, List<Class<?>> repositoryInterfaces) {
        repositoryInterfaces.stream()
                .filter(Class::isInterface)
                .forEach(repoInterface -> {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(repoInterface);
                    enhancer.setCallbackType(MethodInterceptor.class);
                    Class<?> repoClass = enhancer.createClass();

                    Enhancer.registerStaticCallbacks(repoClass, new MethodInterceptor[]{(object, method, args, methodProxy) -> {
                        if (method.isAnnotationPresent(Select.class)) {
                            // TODO 删掉
                            Class<?> returnType = getBeanHandlerType(method);
                            String sql = method.getAnnotation(Select.class).value();
                            Object[] params = getParams(method.getAnnotation(Select.class).value(), args);
                            // get
                            if (List.class.isAssignableFrom(method.getReturnType())) {
                                return queryRunner.query(convertSql(sql), new BeanListHandler<>(returnType), params);
                            } else {
                                return queryRunner.query(convertSql(sql), new BeanHandler<>(returnType), params);
                            }
                        } else if (method.isAnnotationPresent(Update.class)) {
                            String sql = method.getAnnotation(Update.class).value();
                            Object[] params = getParams(method.getAnnotation(Update.class).value(), args);

                            return Integer.valueOf(queryRunner.update(convertSql(sql), params));
                        } else if (method.isAnnotationPresent(Insert.class)) {
                            String sql = method.getAnnotation(Insert.class).value();
                            Object[] params = getParams(method.getAnnotation(Insert.class).value(), args);
                            return Integer.valueOf(queryRunner.update(convertSql(sql), params));
                        } else if (method.isAnnotationPresent(Delete.class)) {
                            String sql = method.getAnnotation(Delete.class).value();
                            Object[] params = getParams(method.getAnnotation(Delete.class).value(), args);
                            return Integer.valueOf(queryRunner.update(convertSql(sql), params));
                        }
                        // 将构造器拦截了，，，，，，，，，，原来放回的null，所以TMD每次newInstance都返回null
                        return methodProxy.invokeSuper(object, args);
                    }});

                    beanContext.setBean(repoInterface.getSimpleName(), repoClass, BeanScope.singleton);
                });
    }

    private Class<?> getBeanHandlerType(Method method) {
        if (List.class.isAssignableFrom(method.getReturnType())) {
            Type type = method.getGenericReturnType();
            String typeName = type.getTypeName().replaceAll(".+<(.+)>", "$1");
            return ClassHelper.loadClass(typeName);
        }
        return method.getReturnType();
    }

    private String convertSql(String sql) {
        return sql.replaceAll("#\\{(.+?)}", "?");
    }

    private Object[] getParams(String sql, Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        Object obj = args[0];

        if (obj instanceof Map) {
            return getParamFromMap(sql, (Map<String, Object>) obj);
        }
        return getParamFromObject(sql, obj);
    }

    private Object[] getParamFromObject(String sql, Object obj) {
        String[] paramNames = resolveParam(sql);
        Object[] params = new Object[paramNames.length];
        Class<?> cls = obj.getClass();
        for (int i = 0; i < paramNames.length; i++) {
            try {
                Field field = cls.getDeclaredField(paramNames[i]);
                field.setAccessible(true);
                params[i] = field.get(obj);
            } catch (IllegalAccessException e) {
                // TODO 异常处理
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                // TODO 异常处理
                e.printStackTrace();
            }
        }
        return params;
    }

    public Object[] getParamFromMap(String sql, Map<String, Object> map) {
        String[] paramNames = resolveParam(sql);
        Object[] params = new Object[paramNames.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = map.get(paramNames[i]);
        }
        return params;
    }

    /**
     * 解析注解中的参数，并从map中取出
     * @return
     */
    private String[] resolveParam(String sql) {
        Matcher matcher = PARAM_PATTERN.matcher(sql);
        List<String> paramList = new LinkedList<>();
        while(matcher.find()) {
            paramList.add(matcher.group(1));
        }
        String[] paramNames = new String[paramList.size()];
        return paramList.toArray(paramNames);
    }

    /**
     * 由借口生成生成映射类，
     * @return
     */
    private Class<?> generateRepoClass(Class<?> reopInterface) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(reopInterface);
        return null;
    }
}
