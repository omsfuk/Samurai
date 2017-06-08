package cn.omsfuk.samurai.framework.tx;

import cn.omsfuk.samurai.framework.core.annotation.BeanScope;
import cn.omsfuk.samurai.framework.util.annotation.PropertiesFile;
import cn.omsfuk.samurai.framework.core.bean.BeanContext;
import cn.omsfuk.samurai.framework.util.PropertyUtil;
import cn.omsfuk.samurai.framework.util.annotation.Property;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Created by omsfuk on 17-5-31.
 */

@PropertiesFile
public class TransactionContext {

    @Property("jdbc.url")
    private static String JDBC_URL;

    @Property("jdbc.driver")
    private static String JDBC_DRIVER;

    @Property("jdbc.username")
    private static String JDBC_USERNAME;

    @Property("jdbc.password")
    private static String JDBC_PASSWORD;

    static {
        PropertyUtil.attachPropertyFileWithClass(TransactionContext.class);
    }

    public TransactionContext(BeanContext beanContext) {
        BasicDataSource basicDataSource= new BasicDataSource();
        basicDataSource.setUrl(JDBC_URL);
        basicDataSource.setDriverClassName(JDBC_DRIVER);
        basicDataSource.setUsername(JDBC_USERNAME);
        basicDataSource.setPassword(JDBC_PASSWORD);

        beanContext.setBean(TransactionalDataSource.class.getSimpleName(), new TransactionalDataSource(basicDataSource), BeanScope.singleton);
    }
}
