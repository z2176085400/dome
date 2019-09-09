package com.zkz.userloginservice.service;

import com.zkz.responsemapper.ResponseMapper;

public interface LoginService {
    ResponseMapper login(String phone,String password);
    //校验登录有效性
    ResponseMapper checkLogin(String token);
    //注销
    ResponseMapper exitLogin(String token);
}
