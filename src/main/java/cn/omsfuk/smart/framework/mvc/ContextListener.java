package cn.omsfuk.smart.framework.mvc;

import cn.omsfuk.smart.framework.aop.AopHelper;
import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.ioc.IocHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-26.
 */

@WebListener
public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.debug("initilizing context...");
        Stream.of(new Class<?>[] {
                IocHelper.class,
                AopHelper.class,
                ControllerHelper.class
        }).forEach(ClassHelper::initClass);
        LOGGER.debug("initilized completely");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
