package cn.omsfuk.demo.controller;

import cn.omsfuk.demo.service.MainService;
import cn.omsfuk.smart.framework.core.annotation.Controller;
import cn.omsfuk.smart.framework.core.annotation.Inject;
import cn.omsfuk.smart.framework.mvc.annotation.RequestMapping;
import cn.omsfuk.smart.framework.mvc.annotation.View;
import cn.omsfuk.smart.framework.orm.User;

/**
 * Created by omsfuk on 17-5-27.
 */

@Controller
public class MainController {

    @Inject
    private SecondController secondController;

    @Inject
    private MainService mainService;

    @RequestMapping("/hello")
    public String index() {
        return "hello";
    }

    public void test() {
        System.out.println("test");
    }

    @RequestMapping("/index")
    @View("json")
    public User index2() {
        mainService.insertUser();
        return new User("omsfuk", "admin");
    }
}