package cn.omsfuk.samurai.framework.mvc;

import cn.omsfuk.samurai.framework.core.bean.BeanContextManager;
import cn.omsfuk.samurai.framework.orm.OrmContext;
import cn.omsfuk.samurai.framework.util.BannerUtil;
import cn.omsfuk.samurai.framework.util.PropertyUtil;
import cn.omsfuk.samurai.framework.util.annotation.PropertiesFile;
import cn.omsfuk.samurai.framework.util.annotation.Property;
import cn.omsfuk.samurai.framework.core.bean.BeanContext;
import cn.omsfuk.samurai.framework.core.bean.impl.DefaultBeanContext;
import cn.omsfuk.samurai.framework.tx.TransactionContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by omsfuk on 17-5-26.
 */

@WebListener
@PropertiesFile
public class ContextListener implements ServletContextListener {

    private static final String MVC_ASPECT = "cn.omsfuk.samurai.framework.mvc.aspect";

    private static BeanContext beanContext;

    @Property("component.scan.path")
    private static String scanPackage;

    public static BeanContext getBeanContext() {
        return beanContext;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        PropertyUtil.attachPropertyFileWithClass(ContextListener.class);
        // 显示banner
        BannerUtil.printBanner("/banner.txt", System.out);
        beanContext = new DefaultBeanContext(scanPackage, MVC_ASPECT);
        BeanContextManager.set(beanContext);

        new TransactionContext(beanContext);
        new OrmContext(beanContext);
        new ControllerContext(beanContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
