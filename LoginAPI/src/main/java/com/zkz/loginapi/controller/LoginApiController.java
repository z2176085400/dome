package com.zkz.loginapi.controller;

import com.zkz.config.ProjectConfig;
import com.zkz.loginapi.service.LoginService;
import com.zkz.responsemapper.ResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
@RestController
public class LoginApiController {
    @Autowired
    private LoginService loginService;

    //登录
    //登录
    @PostMapping("/login/login.do")
    ResponseMapper login(@RequestParam("phone")String phone, @RequestParam("password")String password){
        return loginService.login(phone, password);
    }
    //检查是否有效
    @GetMapping("/login/checklogin.do")
    ResponseMapper check(HttpServletRequest request){
        return loginService.check(request.getHeader(ProjectConfig.TOKENHEAD));
    }
    //注销
    @GetMapping("/login/exit.do")
    ResponseMapper exit(HttpServletRequest request){
        return loginService.exit(request.getHeader(ProjectConfig.TOKENHEAD));
    }
}
