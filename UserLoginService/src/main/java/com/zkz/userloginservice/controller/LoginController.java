package com.zkz.userloginservice.controller;


import com.zkz.responsemapper.ResponseMapper;
import com.zkz.userloginservice.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *@Author feri
 *@Date Created in 2019/6/14 16:16
 */
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    //登录
    @PostMapping("/login/login.do")
    public ResponseMapper login(@RequestParam("phone")String phone, @RequestParam("password")String password){
        return loginService.login(phone, password);
    }
    //检查是否有效
    @GetMapping("/login/checklogin.do")
    public ResponseMapper check(@RequestParam("token")String token){
        return loginService.checkLogin(token);
    }
    //注销
    @GetMapping("/login/exit.do")
    public ResponseMapper exit(@RequestParam("token")String token){
        return loginService.exitLogin(token);
    }
}
