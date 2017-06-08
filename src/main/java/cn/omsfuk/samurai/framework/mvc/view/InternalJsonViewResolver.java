package cn.omsfuk.samurai.framework.mvc.view;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by omsfuk on 17-5-26.
 */
public class InternalJsonViewResolver implements ResponseView {

    private static final Gson gson = new Gson();

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object obj) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(obj));
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
