package com.tmall.controller.login;

import com.liufeng.BaseResponse.ServerResponse;
import com.tmall.Entity.User;
import com.tmall.resultVo.UserVo;
import com.tmall.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login.do")
    public ServerResponse login(@RequestBody User user){
        UserVo userVo = new UserVo();
        user = (User)userService.login(user.getUsername(),user.getPassword()).getData();
        if (user != null)
            BeanUtils.copyProperties(user,userVo);
        return user == null ? ServerResponse.createByError() : ServerResponse.createBySuccess(userVo);
    }
}
