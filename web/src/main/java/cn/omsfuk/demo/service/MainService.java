package cn.omsfuk.demo.service;

import cn.omsfuk.demo.UserRepository;
import cn.omsfuk.smart.framework.core.annotation.Inject;
import cn.omsfuk.smart.framework.core.annotation.Service;
import cn.omsfuk.smart.framework.orm.User;
import cn.omsfuk.smart.framework.tx.annotation.Transactional;

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
        throw new RuntimeException();
    }
}
