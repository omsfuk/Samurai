package cn.omsfuk.demo.controller;

import cn.omsfuk.smart.framework.mvc.annotation.Controller;
import cn.omsfuk.smart.framework.mvc.annotation.RequestMapping;

/**
 * Created by omsfuk on 17-5-27.
 */

@Controller
public class MainController {

    @RequestMapping("index")
    public String index() {
        return "hello";
    }

    @RequestMapping("/index")
    public String index2() {
        return "hello";
    }
}
