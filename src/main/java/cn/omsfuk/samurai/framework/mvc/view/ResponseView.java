package cn.omsfuk.samurai.framework.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by omsfuk on 17-5-26.
 */
public interface ResponseView {

    void render(HttpServletRequest request, HttpServletResponse response, Object obj);
}
