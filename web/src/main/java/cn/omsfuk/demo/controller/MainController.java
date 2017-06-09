package cn.omsfuk.demo.controller;

import cn.omsfuk.demo.service.MainService;
import cn.omsfuk.samurai.framework.core.annotation.Controller;
import cn.omsfuk.samurai.framework.core.annotation.Inject;
import cn.omsfuk.samurai.framework.mvc.Model;
import cn.omsfuk.samurai.framework.mvc.annotation.RequestMapping;
import cn.omsfuk.samurai.framework.mvc.annotation.View;
import cn.omsfuk.demo.User;

import java.util.List;

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
    public String index(Model model) {
        model.setAttribute("name", "omsfuk");
        return "hello";
    }

    public void test() {
        System.out.println("test");
    }

    @RequestMapping("/index")
    @View("json")
    public List<User> index2() {
        return mainService.getUsers();
    }
}