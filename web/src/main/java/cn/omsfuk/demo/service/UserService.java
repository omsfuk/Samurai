package cn.omsfuk.demo.service;

import cn.omsfuk.demo.UserRepository;
import cn.omsfuk.samurai.framework.core.annotation.Inject;
import cn.omsfuk.samurai.framework.core.annotation.Service;
import cn.omsfuk.demo.User;
import cn.omsfuk.samurai.framework.tx.annotation.Transactional;

/**
 * Created by omsfuk on 17-6-1.
 */

@Service
@Transactional
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public void insertUser() {
        userRepository.insertUser(new User("omsfukTxx", "admin"));
    }
}
