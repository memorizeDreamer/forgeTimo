package com.tmall.service;

import com.liufeng.BaseResponse.ResponseCode;
import com.liufeng.BaseResponse.ServerResponse;
import com.liufeng.util.MD5Util;
import com.tmall.Entity.ApplicationInfo;
import com.tmall.Entity.CodeString;
import com.tmall.Entity.User;
import com.tmall.constants.Const;
import com.tmall.repository.ApplicationInfoRepository;
import com.tmall.repository.CodeStringRepository;
import com.tmall.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tmall.util.*;

@Slf4j
@Service
public class UserService {
    @Value("${file.picture.path}")
    public String fileRootPath;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeStringRepository codeStringRepository;

    @Autowired
    private ApplicationInfoRepository applicationInfoRepository;

    @Value("${code.string.max}")
    private Integer codeStringMaxNums;

    private final static String USER_HEAD_IMAGE = "http://47.103.85.203:8090/mobile/user/get_user_image.do?username=";

    @Value("${config.key.max_code_num}")
    private String maxCodeNumKey;

    public ServerResponse login(String username, String password, String mobileNum) {
        // 用户名密码登录
        if (StringUtils.isBlank(mobileNum)){
            ServerResponse validResponse = this.checkValid(username,password);
            if (!validResponse.isSuccess()) {
                return validResponse;
            }
            String md5Password = MD5Util.MD5Encode(password,"UTF-8");
            User mobileUser  = userRepository.findUserByUsernameAndPassword(username,md5Password);
            //更新登陆时间
            userRepository.updateTime(System.currentTimeMillis(),username);
            mobileUser.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
            return ServerResponse.createBySuccess("登录成功",mobileUser);
        } else {
            // 手机号登录
            ServerResponse validResponse = this.checkMobileValid(mobileNum,password);
            if (!validResponse.isSuccess()) {
                return validResponse;
            }
            String md5Password = MD5Util.MD5Encode(password,"UTF-8");
            User mobileUser  = userRepository.findUserByMobileAndPassword(mobileNum,md5Password);
            //更新登陆时间
            userRepository.updateTime(System.currentTimeMillis(),username);
            mobileUser.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
            return ServerResponse.createBySuccess("登录成功",mobileUser);
        }
    }

    /**
     * 普通用户注册
     * @param user
     * @param anotherPassword 确认输入框内的密码
     * @param codeString 验证码
     */
    public ServerResponse register(User user, String anotherPassword, String codeString){
        //检测用户名是否合法
        ServerResponse validResponse = this.checkValid(user.getUsername(),anotherPassword, Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //检测手机号是否已经注册
        if (!checkMobileIsResgister(user.getPhoneNum())){
            return ServerResponse.createByErrorMessage("该手机号已经注册");
        }
        //检测密码6-20位,两次密码是否一致
        validResponse = this.checkValid(user.getPassword(),anotherPassword,Const.PASSWORD);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkSmsCodeString(user.getPhoneNum(), codeString,Const.REGISTER);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        //MD5加密
        user.setPassword(MD5Util.MD5Encode(user.getPassword(),"UTF-8"));
        user.setCreateDate(System.currentTimeMillis());
        user.setUpdateDate(System.currentTimeMillis());
        userRepository.save(user);
        // 注册成功生成 clienId和secret
        ApplicationInfo applicationInfo = new ApplicationInfo(UUID.randomUUID().toString(),UUID.randomUUID().toString(),System.currentTimeMillis(),user.getUsername());
        applicationInfoRepository.save(applicationInfo);
        return ServerResponse.createBySuccessMessage("注册成功");
    }


    /**
     * 检测手机号是否已经注册
     * @param mobileNum
     * @return
     */
    public Boolean checkMobileIsResgister(String mobileNum){
        User mobileUser = userRepository.findUserByMobile(mobileNum);
        return mobileUser == null;
    }

    public ServerResponse updateUserInfo(User user, HttpSession session){
        user.setCreateDate(System.currentTimeMillis());
        userRepository.updateUserInfo(user.getUsername(),user.getEmail(),System.currentTimeMillis(),user.getId());
        session.removeAttribute(Const.CURRENT_USER);
        User sessionUser = userRepository.findUserById(user.getId());
        sessionUser.setPassword(StringUtils.EMPTY);
        session.setAttribute(Const.CURRENT_USER, sessionUser);
        return ServerResponse.createBySuccessMessage("更新成功");
    }


    /**
     * 校验用户名和密码
     * @param mobileNum
     * @param password
     * @return
     */
    public ServerResponse<String> checkMobileValid(String mobileNum, String password) {
        if (org.apache.commons.lang3.StringUtils.isBlank(mobileNum)) {
            return ServerResponse.createByErrorMessage("手机号不能为空");
        }
        log.info(mobileNum+":current user attemp to login!");
        User user = null;
        try {
            user = userRepository.findUserByMobile(mobileNum);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find user: "+mobileNum);
        }
        if (user == null) {
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        if (StringUtils.isBlank(password)) {
            return ServerResponse.createByErrorMessage("密码不能为空");
        }
        String md5Password = MD5Util.MD5Encode(password,"UTF-8");
        try {
            user  = userRepository.findUserByMobileAndPassword(mobileNum,md5Password);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find user: "+mobileNum);
            user = null;
        }
        if (user==null) {
            return ServerResponse.createByErrorMessage("账号或密码错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 校验用户名和密码
     * @param username
     * @param password
     * @return
     */
    public ServerResponse<String> checkValid(String username, String password) {
        if (org.apache.commons.lang3.StringUtils.isBlank(username)) {
            return ServerResponse.createByErrorMessage("账号不能为空");
        }
        log.info(username+":current user attemp to login!");
        User user = null;
        try {
            user = userRepository.findUserByUsername(username);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find user: "+username);
        }
        if (user == null) {
            return ServerResponse.createByErrorMessage("该账号不存在");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(password)) {
            return ServerResponse.createByErrorMessage("密码不能为空");
        }
        String md5Password = MD5Util.MD5Encode(password,"UTF-8");
        try {
            user  = userRepository.findUserByUsernameAndPassword(username,md5Password);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find user: "+username);
            user = null;
        }
        if (user == null) {
            return ServerResponse.createByErrorMessage("账号或密码错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }


    public ServerResponse<String> checkValid(String str,String anotherPassword,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if("mobilenum".equals(type)){
                //限定手机号为全数据，且为11位
                if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
                    return ServerResponse.createByErrorMessage("手机号不能为空");
                }
                if(str.length()!=11 && !isNumeric(str)) {
                    return ServerResponse.createByErrorMessage("手机号格式错误");
                }
                User user = null;
                try {
                    user = userRepository.findUserByMobile(str);
                } catch (EmptyResultDataAccessException e) {
                    log.error("can not find mobilenum: "+str);
                    user = null;
                }
                if(user != null){
                    return ServerResponse.createByErrorMessage("该手机号已经注册");
                }
            }
            if ("username".equals(type)){
                if (org.apache.commons.lang3.StringUtils.isBlank(str)) {
                    return ServerResponse.createByErrorMessage("用户名不能为空");
                }
                if (!checkoutIfIsNumOrChar(str)){
                    return ServerResponse.createByErrorMessage("用户名必须是数字和字母");
                }
                if(str.length() < 6 || str.length() > 11) {
                    return ServerResponse.createByErrorMessage("用户名格式错误");
                }
                User user = null;
                try {
                    user = userRepository.findUserByUsername(str);
                } catch (EmptyResultDataAccessException e) {
                    log.error("can not find username: "+str);
                    user = null;
                }
                if(user != null){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if("password".equals(type)){
                //两次密码是否一致,密码6-16位
                if (org.apache.commons.lang3.StringUtils.isBlank(str)||org.apache.commons.lang3.StringUtils.isBlank(anotherPassword)) {
                    return ServerResponse.createByErrorMessage("密码不能为空");
                }
                if (!str.equals(anotherPassword)) {
                    return ServerResponse.createByErrorMessage("两次密码不一致");
                }
                if (str.length()<6) {
                    return ServerResponse.createByErrorMessage("密码不能少于6位");
                }
                if (str.length()>16) {
                    return ServerResponse.createByErrorMessage("密码不能超过20位");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public static void main(String[] args){
        System.out.println(checkoutIfIsNumOrChar("21312aa"));
    }

    /**
     * 检测用户名是否为数字和字母组合
     * @param text
     * @return
     */
    private static Boolean checkoutIfIsNumOrChar(String text){
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,11}$";
        return text.matches(regex);
    }

    /*
     * 随机生成4位验证码
     * 调用短信接口，发送验证
     * 并存入验证码
     */
    public ServerResponse sendSmsCodeString(CodeString codeStringModel){
        if (org.apache.commons.lang3.StringUtils.isBlank(codeStringModel.getMobileNum())) {
            return ServerResponse.createByErrorMessage("手机号不能为空");
        }
        if (!checkMobileVaild(codeStringModel.getMobileNum())) {
            return ServerResponse.createByErrorMessage("手机号不合法");
        }
        List<CodeString> codeStringList = codeStringRepository.findAllByMobileNumAndCreateTimeAfter(codeStringModel.getMobileNum(), DateUtil.beforeHourDate(new Date(),1));
        int codeStringSendNums = codeStringList.size();
        log.info("{}已发送验证码次数{}",codeStringModel.getMobileNum(),codeStringSendNums);
        if (codeStringList != null && codeStringMaxNums < codeStringSendNums){
            return ServerResponse.createByErrorMessage("验证码次数过多");
        }
        int max=9999;
        int min=1000;
        Random random = new Random();
        String codeString = ""+(random.nextInt(max)%(max-min+1) + min);
        log.info("用户 "+codeStringModel.getMobileNum()+"的短信验证码为："+codeString);
        // 调用短信接口,发送短信
        String result = SmsInterface.sendTplSms(codeStringModel.getMobileNum(), codeString);
        log.info("发送短信验证的状态："+result);
        codeStringModel.setCodeString(codeString);
        codeStringModel.setCreateTime(System.currentTimeMillis());
        codeStringModel.setUpdateTime(System.currentTimeMillis());
        codeStringRepository.save(codeStringModel);
        return ServerResponse.createBySuccessMessage("验证码已发送");
    }

    /*
     * 判断手机号是否符合规范
     */
    private boolean checkMobileVaild(String mobileNum){
        String regexString = "^1[0-9]{10}$";
        Pattern p = Pattern.compile(regexString);
        Matcher m = p.matcher(mobileNum);
        return m.matches() && !"0".equals(mobileNum.charAt(1));
    }

    /*
     * 用户输入收到的验证码
     * 验证是否正确，时间是否失效,5分钟
     * 验证成功返回token
     */
    public ServerResponse checkSmsCodeString(String mobileNum,String enterSmsCodeString,String type){
        CodeString codeString = codeStringRepository.findFirstByMobileNumOrderByCreateTimeDesc(mobileNum);
        if (codeString==null) {
            return ServerResponse.createByErrorMessage("请确认输入信息是否正确");
        }
        Long createTime = codeString.getCreateTime();
        Long currentTime = System.currentTimeMillis();
        if (currentTime - createTime > 300000){
            return ServerResponse.createByErrorMessage("验证码已失效");
        }
        if(!(org.apache.commons.lang3.StringUtils.isBlank(enterSmsCodeString))){
            if (Const.FORGET_PASSWORD.equals(type)) {
                if (enterSmsCodeString.equals(codeString.getCodeString())) {
                    String forgetToken = UUID.randomUUID().toString();
                    return ServerResponse.createBySuccess(forgetToken);
                }
                return ServerResponse.createByErrorMessage("验证码不正确，请重新输入");
            }
            if (Const.REGISTER.equals(type)) {
                log.debug(codeString.getCodeString()+"update time : "+codeString.getCreateTime());

                if (enterSmsCodeString.equals(codeString.getCodeString())) {
                    return ServerResponse.createBySuccessMessage("验证码验证成功");
                }
                return ServerResponse.createByErrorMessage("验证码不正确，请重新输入");
            }
        }
        return ServerResponse.createByErrorMessage("验证码不能为空");
    }

    /*
     * @param passwordNew :传入新密码
     * @param forgetToken :token
     */
    public ServerResponse forgetResetPassword(User user, String passwordNew,String codeString){
        ServerResponse serverResponse = checkSmsCodeString(user.getPhoneNum(),codeString,Const.FORGET_PASSWORD);
        if (serverResponse.getStatus() != ResponseCode.SUCCESS.getCode()){
            return serverResponse;
        }
        String username = user.getUsername();
        User existUser = null;
        try {
            existUser = userRepository.findUserByUsername(username);
        } catch (EmptyResultDataAccessException e) {
            log.error("can not find codestring: "+username);
            existUser = null;
        }
        if (existUser == null) {
            return ServerResponse.createByErrorMessage("该账号不存在");
        }
        if (passwordNew.length() > 16 || passwordNew.length() < 6){
            return ServerResponse.createByErrorMessage("密码长度不在6-16");
        }
        String md5Password  = MD5Util.MD5Encode(passwordNew,"UTF-8");
        int result = userRepository.updatePassword(System.currentTimeMillis(),md5Password,username);
        if (result == 1) {
            return ServerResponse.createBySuccessMessage("修改密码成功");
        } else {
            return ServerResponse.createByErrorMessage("修改密码失败，用户名不存在");
        }
    }

    public ServerResponse<String> resetPassword(String passwordNew,User user,String codeString){
        ServerResponse serverResponse = checkSmsCodeString(user.getPhoneNum(),codeString,Const.FORGET_PASSWORD);
        if (serverResponse.getStatus() != ResponseCode.SUCCESS.getCode()){
            return serverResponse;
        }
        if (passwordNew.length() > 16 || passwordNew.length() < 6){
            return ServerResponse.createByErrorMessage("密码长度不在6-16");
        }
        String password = MD5Util.MD5Encode(passwordNew,"UTF-8");
        userRepository.updatePassword(System.currentTimeMillis(),password,user.getUsername());
        return ServerResponse.createBySuccessMessage("密码更新成功");
    }

    public ServerResponse uploadUserImage(String username, MultipartFile file){
        String filepath = fileRootPath + username + "/head/head_image.jpg";
        try {
            File imageFile = new File(filepath);
            if (imageFile.exists()){
                imageFile.delete();
            }
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            try {
                File result = new File(filepath);//要写入的图片
                File dir = result.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                try {
                    result.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream out = new FileOutputStream(result);
                int n = 0;
                byte[] bb = new byte[1024];
                while ((n = fileInputStream.read(bb)) != -1) {
                    out.write(bb, 0, n);
                }
                out.close();
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return ServerResponse.createByError("上传的图片为空");
        }
        // 上传完成之后，需要更新用户图像记录
        userRepository.updateUserImage(USER_HEAD_IMAGE+username,System.currentTimeMillis(),username);
        return ServerResponse.createBySuccessMessage("上传图像成功");
    }

    public void getPicture(String username, HttpServletResponse response){
        try {
            String filepath = fileRootPath + username  + "/head/head_image.jpg";
            File file = new File(filepath);
            @SuppressWarnings("resource")
            InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024]; // 图片文件流缓存池
            while (is.read(buffer) != -1) {
                os.write(buffer);
            }
            os.flush();
        } catch (IOException ioe) {
            log.error(ioe.toString());
        }
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        return pattern.matcher(str).matches();
    }
}
