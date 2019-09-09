package com.zkz.loginapi.service;

import com.zkz.responsemapper.ResponseMapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "LoginProvider")
public interface LoginService {
    //登录
    @PostMapping("/login/login.do")
    ResponseMapper login(@RequestParam("phone")String phone, @RequestParam("password")String password);
    //检查是否有效
    @GetMapping("/login/checklogin.do")
    ResponseMapper check(@RequestParam("token")String token);
    //注销
    @GetMapping("/login/exit.do")
    ResponseMapper exit(@RequestParam("token")String token);
}
