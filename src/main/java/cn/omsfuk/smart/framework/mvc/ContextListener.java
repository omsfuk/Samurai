package cn.omsfuk.smart.framework.mvc;

import cn.omsfuk.smart.framework.helper.PropertyHelper;
import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import cn.omsfuk.smart.framework.core.BeanContext;
import cn.omsfuk.smart.framework.core.impl.DefaultBeanContext;
import cn.omsfuk.smart.framework.orm.OrmContext;
import cn.omsfuk.smart.framework.tx.TransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by omsfuk on 17-5-26.
 */

@WebListener
@PropertiesFile
public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class);

    private static BeanContext beanContext;

    @Property("component.scan.path")
    private static String scanPackage;

    public static BeanContext getBeanContext() {
        return beanContext;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.debug("initilizing context...");
        PropertyHelper.attachPropertyFileWithClass(ContextListener.class);

        beanContext = new DefaultBeanContext(scanPackage);
        DefaultBeanContext.set((DefaultBeanContext) beanContext);

        new TransactionContext(beanContext);
        new OrmContext(beanContext);
        new ControllerHelper(beanContext);

        LOGGER.debug("initilized completely");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
