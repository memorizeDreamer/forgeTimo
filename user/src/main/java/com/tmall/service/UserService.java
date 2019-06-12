package com.tmall.service;

import com.liufeng.BaseResponse.ServerResponse;

public interface UserService {
    ServerResponse login(String username, String password);

    ServerResponse logout();

    ServerResponse register();

    ServerResponse getUserInfo();

    ServerResponse changeUserInfo();
}
