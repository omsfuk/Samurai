package cn.omsfuk.demo.controller;

import cn.omsfuk.smart.framework.mvc.annotation.Controller;
import cn.omsfuk.smart.framework.mvc.annotation.RequestMapping;
import cn.omsfuk.smart.framework.mvc.annotation.View;

/**
 * Created by omsfuk on 17-5-27.
 */

@Controller
public class MainController {

    @RequestMapping("/hello")
    public String index() {
        return "hello";
    }

    @RequestMapping("/index")
    @View("json")
    public String index2(String name) {
        return "hello";
    }
}