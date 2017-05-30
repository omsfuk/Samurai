package cn.omsfuk.smart.framework.mvc;

import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.helper.PropertyHelper;
import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import cn.omsfuk.smart.framework.ioc.BeanContext;
import cn.omsfuk.smart.framework.ioc.DefaultBeanContext;
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

    @Property("component.scan.path")
    private static String scanPackage;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.debug("initilizing context...");
        PropertyHelper.attachPropertyFileWithClass(ContextListener.class);
        BeanContext beanContext = new DefaultBeanContext(scanPackage);
        new ControllerHelper(beanContext);
        LOGGER.debug("initilized completely");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
