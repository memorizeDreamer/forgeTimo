package com.tmall.admin.controller;

import com.liufeng.BaseResponse.ServerResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @RequestMapping("/fortest")
    public ServerResponse forTest(){
        return ServerResponse.createBySuccess();
    }
}
