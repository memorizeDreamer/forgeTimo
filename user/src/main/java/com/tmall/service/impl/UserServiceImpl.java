package com.tmall.service.impl;

import com.liufeng.BaseResponse.ServerResponse;
import com.tmall.Entity.User;
import com.tmall.repository.UserRepository;
import com.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public ServerResponse login(String username, String password) {
        User user = userRepository.findUserByUsernameAndPassword(username,password);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse logout() {
        return null;
    }

    @Override
    public ServerResponse register() {
        return null;
    }

    @Override
    public ServerResponse getUserInfo() {
        return null;
    }

    @Override
    public ServerResponse changeUserInfo() {
        return null;
    }
}
