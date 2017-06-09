package cn.omsfuk.samurai.framework.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by omsfuk on 2017/6/9.
 */
public class Model {

    private HttpServletRequest request;

    public Model(HttpServletRequest request) {
        this.request = request;
    }

    public void setAttribute(String key, Object attr) {
        request.setAttribute(key, attr);
    }
}
