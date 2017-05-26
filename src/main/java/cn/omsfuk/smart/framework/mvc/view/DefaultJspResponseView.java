package cn.omsfuk.smart.framework.mvc.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by omsfuk on 17-5-26.
 */
public class DefaultJspResponseView implements ResponseView {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJspResponseView.class);

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object obj) {
        if(!(obj instanceof String)) {
            RuntimeException exception = new RuntimeException("fail to render view, due to an error return obj");
            LOGGER.error("fail to render view, due to an error return obj", exception);
            throw exception;
        }

        try {
            request.getRequestDispatcher("WEB-INF/" + obj).forward(request, response);
        } catch (IOException | ServletException e) {
            RuntimeException exception = new RuntimeException("fail to render view, view name doesn't exist or some other reason");
            LOGGER.error("fail to render view, view name doesn't exist or some other reason", exception);
            throw exception;
        }
    }
}
