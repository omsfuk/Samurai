package cn.omsfuk.smart.framework.mvc;

import cn.omsfuk.smart.framework.helper.AnnotationHelper;
import cn.omsfuk.smart.framework.helper.ClassHelper;
import cn.omsfuk.smart.framework.helper.PropertyHelper;
import cn.omsfuk.smart.framework.ioc.BeanHelper;
import cn.omsfuk.smart.framework.mvc.annotation.Controller;
import cn.omsfuk.smart.framework.mvc.annotation.RequestMapping;
import cn.omsfuk.smart.framework.mvc.annotation.View;
import cn.omsfuk.smart.framework.mvc.view.DefaultJspResponseView;
import cn.omsfuk.smart.framework.mvc.view.ResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by omsfuk on 17-5-27.
 */
public final class ControllerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    static {
        LOGGER.debug("init controller...");
        BeanHelper.setBean(new DefaultJspResponseView("WEB-INF/view/", ".jsp"));
        List<Object> controllers = BeanHelper.getBeanByAnnotation(Controller.class);
        controllers.stream().forEach(controller -> {
            // TODO 无法获得私有方法，只能获得公有方法，或许可以通过cglib改变修饰词实现访问私有方法
            Stream.of(controller.getClass().getDeclaredMethods())
                .filter(method -> AnnotationHelper.isAnnotationPresent(method, RequestMapping.class))
                .forEach(method -> {
                    ResponseView view = null;
                    if(AnnotationHelper.isAnnotationPresent(method, View.class)) {
                        Class<?> cls = ClassHelper.loadClass(PropertyHelper.getProperty("smart.properties",
                                "response.view." + ((View) AnnotationHelper.getAnnotation(method, View.class)).value()));
                        if(BeanHelper.getBean(cls) == null) {
                            try {
                                view = (ResponseView) cls.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                LOGGER.error("can't init view : {}",
                                        PropertyHelper.getProperty("smart.properties",
                                                "response.view." + ((View) AnnotationHelper.getAnnotation(method, View.class)).value()));
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
                            new RequestHandler(view, controller, method, ((RequestMapping) AnnotationHelper.getAnnotation(method, RequestMapping.class)).value()));
                    LOGGER.debug("add mapping [{}] to controller [{}]", ((RequestMapping) AnnotationHelper.getAnnotation(method, RequestMapping.class)).value(), controller.getClass().getName());
                });
        });
    }

}
