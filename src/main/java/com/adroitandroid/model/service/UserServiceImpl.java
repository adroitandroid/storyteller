package com.adroitandroid.model.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by pv on 02/12/16.
 */
@Component("userService")
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public int setUsername(Long userId, String username) {
        return userRepository.setUsername(userId, username);
    }
}
