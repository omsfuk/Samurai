package cn.omsfuk.demo.service;

import cn.omsfuk.demo.UserRepository;
import cn.omsfuk.samurai.framework.core.annotation.Inject;
import cn.omsfuk.samurai.framework.core.annotation.Service;
import cn.omsfuk.demo.User;
import cn.omsfuk.samurai.framework.tx.annotation.Transactional;

import java.util.List;

/**
 * Created by omsfuk on 17-5-31.
 */

@Service
@Transactional
public class MainService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Transactional
    public void insertUser() {
        userRepository.insertUser(new User("omsfukTx", "admin"));
        userService.insertUser();
//        throw new RuntimeException();
    }

    @Transactional
    public List<User> getUsers() {
        return userRepository.getUsers();
    }
}
