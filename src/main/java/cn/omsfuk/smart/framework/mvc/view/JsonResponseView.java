package cn.omsfuk.smart.framework.mvc.view;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by omsfuk on 17-5-26.
 */
public class JsonResponseView implements ResponseView {

    private static final Gson gson = new Gson();

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object obj) {
        try {
            response.getWriter().write(gson.toJson(obj));
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
