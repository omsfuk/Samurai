package cn.omsfuk.samurai.framework.mvc;

import cn.omsfuk.samurai.framework.util.AnnotationUtil;
import cn.omsfuk.samurai.framework.util.ClassUtil;
import cn.omsfuk.samurai.framework.util.PropertyUtil;
import cn.omsfuk.samurai.framework.core.bean.BeanContext;
import cn.omsfuk.samurai.framework.core.annotation.BeanScope;
import cn.omsfuk.samurai.framework.core.annotation.Controller;
import cn.omsfuk.samurai.framework.util.annotation.PropertiesFile;
import cn.omsfuk.samurai.framework.util.annotation.Property;
import cn.omsfuk.samurai.framework.mvc.annotation.RequestMapping;
import cn.omsfuk.samurai.framework.mvc.annotation.View;
import cn.omsfuk.samurai.framework.mvc.view.DefaultJspResponseView;
import cn.omsfuk.samurai.framework.mvc.view.ResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-27.
 */

@PropertiesFile
public final class ControllerContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    @Property("component.scan.path")
    private static String SCAN_PACKAGE;

    static {
        PropertyUtil.attachPropertyFileWithClass(ControllerContext.class);
    }

    public ControllerContext(BeanContext beanContext) {
        LOGGER.debug("init controller...");
        beanContext.setBean("DefaultJspResponseView", new DefaultJspResponseView("WEB-INF/view/", ".jsp"), BeanScope.singleton);
        List<Class<?>> controllers = ClassUtil.loadClassByAnnotation(Controller.class, SCAN_PACKAGE);
        controllers.stream().forEach(controller -> {
            Stream.of(controller.getDeclaredMethods())
                    .filter(method -> AnnotationUtil.isAnnotationPresent(method, RequestMapping.class))
                    .forEach(method -> {
                        ResponseView view = null;
                        if(AnnotationUtil.isAnnotationPresent(method, View.class)) {
                            Class<?> cls = ClassUtil.loadClass(PropertyUtil.getProperty("samurai.properties",
                                    "response.view." + method.getAnnotation(View.class).value()));
                            if(beanContext.getBean(cls) == null) {
                                try {
                                    view = (ResponseView) cls.newInstance();
                                } catch (InstantiationException | IllegalAccessException e) {
                                    LOGGER.error("can't init view : {}",
                                            PropertyUtil.getProperty("samurai.properties",
                                                    "response.view." + method.getAnnotation(View.class).value()));
                                    throw new RuntimeException(e);
                                }
                                beanContext.setBean(cls.getSimpleName(), cls, BeanScope.singleton);
                            } else {
                                view = (ResponseView) beanContext.getBean(cls);
                            }

                        } else {
                            view = (ResponseView) beanContext.getBean(DefaultJspResponseView.class);
                        }
                        DispatcherServlet.addRequestHandler(
                                new RequestHandler(view, beanContext.getBean(controller), method, method.getAnnotation(RequestMapping.class).value()));
                        LOGGER.debug("add mapping [{}] to controller [{}]", method.getAnnotation(RequestMapping.class).value(), controller.getName());
                    });
        });
        LOGGER.debug("controller initilized complete...");
    }

}
