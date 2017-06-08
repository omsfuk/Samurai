package cn.omsfuk.samurai.framework.mvc;

import cn.omsfuk.samurai.framework.core.InstanceFactory;
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
import cn.omsfuk.samurai.framework.mvc.view.InternalJspViewResolver;
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

    private static InstanceFactory instanceFactory;

    @Property("component.scan.path")
    private static String SCAN_PACKAGE;

    static {
        PropertyUtil.attachPropertyFileWithClass(ControllerContext.class);
    }

    public ControllerContext(BeanContext beanContext) {
        LOGGER.debug("[Samurai] initializing controller context ...");
        instanceFactory = new InstanceFactory(beanContext);
        registerView(beanContext);
        List<Class<?>> controllers = ClassUtil.loadClassByAnnotation(Controller.class, SCAN_PACKAGE);
        controllers.stream().forEach(controller -> {
            Stream.of(controller.getDeclaredMethods())
                    .filter(method -> AnnotationUtil.isAnnotationPresent(method, RequestMapping.class))
                    .forEach(method -> {
                        ResponseView view = null;
                        if(AnnotationUtil.isAnnotationPresent(method, View.class)) {
                            view = (ResponseView) beanContext.getBean(method.getAnnotation(View.class).value());
                        } else {
                            view = (ResponseView) beanContext.getBean("jsp");
                        }
                        DispatcherServlet.addRequestHandler(
                                new RequestHandler(view, beanContext.getBean(controller), method, method.getAnnotation(RequestMapping.class).value()));
                        LOGGER.debug("[Samurai] add mapping [{}] to controller [{}]", method.getAnnotation(RequestMapping.class).value(), controller.getName());
                    });
        });
        LOGGER.debug("[Samurai] controller context initialization complete");
    }

    /**
     * 注册视图
     * @param beanContext
     */
    public void registerView(BeanContext beanContext) {
        // 注册内置JSP解析器
        beanContext.setBean("jsp", new InternalJspViewResolver("WEB-INF/view/", ".jsp"), BeanScope.singleton);
        // 注册自定义的视图解析器
        PropertyUtil.listAllProperties("samurai.properties").forEach((key, value) -> {
            if (key.startsWith("response.view.")) {
                beanContext.setBean(key.substring(14, key.length()), ClassUtil.loadClass(value), BeanScope.singleton);
            }
        });
    }

}
