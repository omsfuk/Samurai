package cn.omsfuk.smart.framework.mvc;

import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.helper.PropertyHelper;
import cn.omsfuk.smart.framework.helper.annotation.PropertiesFile;
import cn.omsfuk.smart.framework.helper.annotation.Property;
import cn.omsfuk.smart.framework.ioc.BeanHelper;
import cn.omsfuk.smart.framework.ioc.annotation.Bean;
import cn.omsfuk.smart.framework.mvc.annotation.Controller;
import cn.omsfuk.smart.framework.mvc.annotation.RequestMapping;
import cn.omsfuk.smart.framework.mvc.annotation.View;
import cn.omsfuk.smart.framework.mvc.view.DefaultJspResponseView;
import cn.omsfuk.smart.framework.mvc.view.ResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-26.
 */

@PropertiesFile
@WebListener
public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class);

    @Property("component.scan.path")
    private static String scanPath;

    {
        PropertyHelper.attachPropertyFileWithObject(this);
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        registerBean();
        initController();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private void registerBean() {
        LOGGER.debug("try to register beans...");
        int beanCount = 0;
        LinkedList<Class<? extends Annotation>> annotations = new LinkedList<Class<? extends Annotation>>();
        annotations.add(Bean.class);
        annotations.add(Controller.class);
        annotations.forEach(annotation -> {
            ClassHelper.getClassesByAnnotation(scanPath, annotation).stream().forEach(cls -> {
                Object bean = BeanHelper.satisfyConstructorDependency(cls);
                BeanHelper.setBean(bean);
                LOGGER.debug("register bean {}", bean.getClass().getName());
            });
        });
        LOGGER.debug("{} bean(s) was register", beanCount);
    }

    private void initController() {
        LOGGER.debug("init controller");
        BeanHelper.setBean(new DefaultJspResponseView("WEB-INF/view/", ".jsp"));
        List<Object> controllers = BeanHelper.getBeanByAnnotation(Controller.class);
        controllers.stream().forEach(controller -> {
            Stream.of(controller.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                    .forEach(method -> {
                        System.out.println(method.getName());
                        ResponseView view = null;
                        if(method.isAnnotationPresent(View.class)) {
                            Class<?> cls = ClassHelper.loadClass(PropertyHelper.getProperty("smart.properties",
                                    "response.view." + method.getAnnotation(View.class).value()));
                            if(BeanHelper.getBean(cls) == null) {
                                try {
                                    view = (ResponseView) cls.newInstance();
                                } catch (InstantiationException | IllegalAccessException e) {
                                    LOGGER.error("can't init view : {}",
                                            PropertyHelper.getProperty("smart.properties",
                                                    "response.view." + method.getAnnotation(View.class).value()));
                                    throw new RuntimeException(e);
                                }
                                BeanHelper.setBean(cls);
                            } else {
                                view = (ResponseView) BeanHelper.getBean(cls);
                            }

                        } else {
                            view = (ResponseView) BeanHelper.getBean(DefaultJspResponseView.class);
                        }

                        DispatcherServlet.addRequestHandler(
                                new RequestHandler(view, controller, method, method.getAnnotation(RequestMapping.class).value()));
                        LOGGER.debug("add mapping [{}] to controller [{}]", method.getAnnotation(RequestMapping.class).value(), controller.getClass().getName());
                    });
        });
    }
}
