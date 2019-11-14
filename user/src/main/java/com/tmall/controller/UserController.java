package com.tmall.controller;

import com.liufeng.BaseResponse.ServerResponse;
import com.tmall.Entity.CodeString;
import com.tmall.Entity.User;
import com.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    private static final String CURRENT_USER = "currentUser";
    /**
     * 用户登录
     * @param mobileUser
     * @param session
     * @return
     */
    @PostMapping(value = "/mobile/user/login.do")
    public ServerResponse login(@RequestBody User mobileUser, HttpSession session){
        String username = mobileUser.getUsername();
        String password = mobileUser.getPassword();
        String mobileNum = mobileUser.getPhoneNum();
        ServerResponse serverResponse = userService.login(username,password,mobileNum);
        if(serverResponse.isSuccess()){
            session.setAttribute(CURRENT_USER,serverResponse.getData());
            session.setMaxInactiveInterval(-1);
            return serverResponse;
        }
        return serverResponse;
    }

    @RequestMapping("/mobile/user/login_status.do")
    public ServerResponse LoginStatus(HttpSession session){
        User mobileUser = (User) session.getAttribute(CURRENT_USER);
        if(mobileUser == null){
            return ServerResponse.createByError("用户未登录");
        }
        return ServerResponse.createBySuccess("用户已登录");
    }

    @RequestMapping(value = "/mobile/user/logout.do")
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(CURRENT_USER);
        return ServerResponse.createBySuccessMessage("用户已退出");
    }

    /**
     * @param user
     * @param anotherPassword 确认框内的密码
     * @param codeString 验证码
     */
    @PostMapping(value = "/mobile/user/register.do")
    public ServerResponse register(@RequestBody User user,
                                   @RequestHeader("anotherPassword") String anotherPassword,
                                   @RequestHeader("codeString") String codeString){
        return userService.register(user, anotherPassword, codeString);
    }

    @RequestMapping(value = "/mobile/user/check_valid.do")
    @ResponseBody
    public ServerResponse checkValid(String str, String anotherPassword, String type){
        return userService.checkValid(str,anotherPassword,type);
    }

    @RequestMapping(value = "/mobile/user/get_user_info.do")
    @ResponseBody
    public ServerResponse getUserInfo(HttpSession session){
        User mobileUser = (User) session.getAttribute(CURRENT_USER);
        if(mobileUser != null){
            return ServerResponse.createBySuccess(mobileUser);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    @PutMapping(value = "/mobile/user/update_user_info.do")
    public ServerResponse updateUserInfo(@RequestBody User mobileUser,HttpSession session){
        User sessionMobileUser = (User) session.getAttribute(CURRENT_USER);
        if(sessionMobileUser == null){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        return userService.updateUserInfo(mobileUser, session);
    }

    /*
     * 忘记密码时，先生成短信验证码
     * 随机生成4位数验证码
     * 存入到data_codeString表
     */
    @PostMapping(value = "/mobile/user/get_smsCodeString.do")
    public ServerResponse getSmsCodeString(@RequestBody CodeString codeStringModel){
        return userService.sendSmsCodeString(codeStringModel);
    }

    /*
     * 验证短信验证码，验证成功返回token给前端
     * 设置新密码时，和密码一起传入校验
     */
    @RequestMapping(value = "/mobile/user/forget_check_smsCodeString.do")
    @ResponseBody
    public ServerResponse forgetCheckSmsCodeString(String username, String enterSmsCodeString, String type){
        return userService.checkSmsCodeString(username,enterSmsCodeString,type);
    }

    @PostMapping(value = "/mobile/user/reset_password.do")
    public ServerResponse resetPassword(HttpSession session,
                                        @RequestHeader ("token") String token,
                                        @RequestHeader("passwordNew") String passwordNew,
                                        @RequestHeader("codeString") String codeString){
        User mobileUser = (User) session.getAttribute(CURRENT_USER);
        if(mobileUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return userService.resetPassword(passwordNew,mobileUser,codeString);
    }

    /*
     * @param username
     * @param passwordNew :新密码
     * @param forgetToken :token
     */
    @RequestMapping(value = "/mobile/user/forget_reset_password.do")
    public ServerResponse forgetRestPassword(@RequestBody User mobileUser,
                                             @RequestHeader("passwordNew") String passwordNew,
                                             @RequestHeader("codeString") String codeString){
        return userService.forgetResetPassword(mobileUser,passwordNew,codeString);
    }

    @PostMapping("/mobile/user/upload_user_image.do")
    public ServerResponse uploadUserImage(@RequestHeader("username") String username,
                                          @RequestBody MultipartFile file){
        return userService.uploadUserImage(username,file);
    }

    @GetMapping("/mobile/user/get_user_image.do")
    public void getUserImage(@RequestParam("username")String username, HttpServletResponse response){
        userService.getPicture(username,response);
    }
}
