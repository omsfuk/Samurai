package cn.omsfuk.samurai.framework.mvc.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by omsfuk on 17-5-26.
 */
public class InternalJspViewResolver implements ResponseView {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalJspViewResolver.class);

    private String prefix;

    private String suffix;

    public InternalJspViewResolver(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object obj) {
        if(!(obj instanceof String)) {
            throw new RuntimeException("fail to render view, due to an error return obj");
        }

        try {
            request.getRequestDispatcher(prefix + obj + suffix).forward(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException("fail to render view, view name doesn't exist or some other reason");
        }
    }
}
