package com.liufeng.controller;

import com.liufeng.entity.UserAccount;
import com.liufeng.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 初始化用户数据
     */
    @Autowired
    public void init(){

        // 为了方便测试,这里添加了两个不同角色的账户
        userRepository.deleteAll();

        UserAccount accountA = new UserAccount();
        accountA.setUsername("admin");
        accountA.setPassword("admin");
        accountA.setRole(0);
        userRepository.save(accountA);

        UserAccount accountB = new UserAccount();
        accountB.setUsername("guest");
        accountB.setPassword("pass123");
        accountB.setRole(1);
        userRepository.save(accountB);
    }

    @GetMapping("/user")
    public Principal user(Principal user){
        return user;
    }
}
