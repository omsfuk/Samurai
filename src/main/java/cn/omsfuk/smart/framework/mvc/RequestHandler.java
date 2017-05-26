package cn.omsfuk.smart.framework.mvc;

import cn.omsfuk.smart.framework.mvc.view.ResponseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by omsfuk on 17-5-26.
 */
public class RequestHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private ResponseView responseView;

    private Object controller;

    private Method method;

    private String patternStr;

    private Pattern pattern;

    public RequestHandler() {}

    public RequestHandler(ResponseView responseView, Object controller, Method method, String pattern) {
        this.responseView = responseView;
        this.controller = controller;
        this.method = method;
        this.patternStr = pattern;
        this.pattern = Pattern.compile(pattern);
    }

    public ResponseView getResponseView() {
        return responseView;
    }

    public void setResponseView(ResponseView responseView) {
        this.responseView = responseView;
    }

    public String getPatternStr() {
        return patternStr;
    }

    public void setPatternStr(String patternStr) {
        this.patternStr = patternStr;
        this.pattern = Pattern.compile(patternStr);
    }

    public void handler(HttpServletRequest request, HttpServletResponse response, Object[] params) {
        Object result = null;
        try {
            result = method.invoke(controller, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("invoke controller method error: {}", this.controller.getClass().getName());
            throw new RuntimeException(e);
        }
        responseView.render(request, response, result);
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
