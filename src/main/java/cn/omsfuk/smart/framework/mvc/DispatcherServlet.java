package cn.omsfuk.smart.framework.mvc;

import cn.omsfuk.smart.framework.core.BeanContext;
import cn.omsfuk.smart.framework.core.BeanContextManager;
import cn.omsfuk.smart.framework.core.impl.DefaultBeanContext;
import cn.omsfuk.smart.framework.core.annotation.BeanScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by omsfuk on 17-5-26.
 */
public class DispatcherServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);

    private static List<RequestHandler> requestHandlers = new LinkedList<>();

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BeanContext beanContext = ContextListener.getBeanContext();
        // 开始
        try {
            LOGGER.debug("request [{}]", req.getRequestURI());
            BeanContextManager.set((DefaultBeanContext) beanContext);
            Optional<RequestHandler> handler = requestHandlers.parallelStream()
                    .filter(requestHandler -> req.getRequestURI().matches(requestHandler.getPatternStr()))
                    .findAny();
            if (!handler.isPresent()) {
                LOGGER.debug("can't find and pattern to {}", req.getRequestURI());
            } else {

                LOGGER.debug("find controller [{}] match mapping [{}]", handler.get().getController().getClass().getName(), req.getRequestURI());

                beanContext.setBean("HttpServletRequest", req, BeanScope.request);
                beanContext.setBean("HttpServletResponse", req, BeanScope.request);
                handler.ifPresent(requestHandler -> requestHandler.handler(req, resp, new Object[]{}));
                LOGGER.debug("request complete [{}]", req.getRequestURI());
            }
        } catch (Exception e) {
            // TODO 异常
            e.printStackTrace();
        } finally {
            beanContext.removeRequestBeans();
            BeanContextManager.remove();
        }

    }

    public static void addRequestHandler(RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
    }
}
