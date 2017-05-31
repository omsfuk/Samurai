package cn.omsfuk.demo.controller;

import cn.omsfuk.demo.UserRepository;
import cn.omsfuk.smart.framework.core.annotation.Controller;
import cn.omsfuk.smart.framework.core.annotation.Inject;
import cn.omsfuk.smart.framework.mvc.annotation.RequestMapping;
import cn.omsfuk.smart.framework.mvc.annotation.View;
import cn.omsfuk.smart.framework.orm.User;

import java.util.List;

/**
 * Created by omsfuk on 17-5-27.
 */

@Controller
public class MainController {

    @Inject
    private SecondController secondController;

    @Inject
    private UserRepository userRepository;

    @RequestMapping("/hello")
    public String index() {
        return "hello";
    }

    public void test() {
        System.out.println("test");
    }

    @RequestMapping("/index")
    @View("json")
    public List<User> index2() {
        User user = new User();
        user.setId(1);
        user = userRepository.getUser(user);
        List<User> users = userRepository.getUsers();
        users.forEach(System.out::println);
        return users;
    }
}