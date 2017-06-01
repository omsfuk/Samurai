package cn.omsfuk.demo.service;

import cn.omsfuk.demo.UserRepository;
import cn.omsfuk.smart.framework.core.annotation.Inject;
import cn.omsfuk.smart.framework.core.annotation.Service;
import cn.omsfuk.smart.framework.orm.User;
import cn.omsfuk.smart.framework.tx.annotation.Transactional;

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
