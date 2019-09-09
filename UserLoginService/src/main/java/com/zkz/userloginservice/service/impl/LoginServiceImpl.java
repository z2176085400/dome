package com.zkz.userloginservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.zkz.config.ProjectConfig;
import com.zkz.entity.User;
import com.zkz.jwt.JwtUtil;
import com.zkz.responsemapper.ResponseMapper;
import com.zkz.userloginservice.dao.UserDao;
import com.zkz.userloginservice.dao.UserLogDao;
import com.zkz.userloginservice.model.LoginToken;
import com.zkz.userloginservice.service.LoginService;
import com.zkz.util.EncryptionUtil;
import com.zkz.util.IdGenerator;
import com.zkz.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserDao userdao;
    @Autowired
    private UserLogDao userLogDao;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private IdGenerator idGenerator;

    @Override
    public ResponseMapper login(String phone, String password) {
        ResponseMapper r;
        if(jedisUtil.exists(ProjectConfig.USERSD+phone)){
            r=ResponseMapper.setERROR("账号锁定中，剩余时间："+(jedisUtil.ttl(ProjectConfig.USERSD+phone)/60));
        } else {
            User user = userdao.selectByPhone(phone);
            if (null != user ) {
              /*  if (Objects.equals(user.getPassword(), EncryptionUtil.RSADec(ProjectConfig.PASSRSAPRI,password))){*/
                if (user.getPassword().equals(password)){
                    LoginToken loginToken=new LoginToken();
                    loginToken.setPhone(phone);
                    loginToken.setUid(user.getId());
                    loginToken.setId(idGenerator.nextId()+"");
                    String token = JwtUtil.createJWT(loginToken.getId(), JSON.toJSONString(loginToken));
                    jedisUtil.setex(ProjectConfig.TOKENPHONE+phone,1800,token);
                    jedisUtil.setex(ProjectConfig.TOKENJWT+token,1800, JSON.toJSONString(user));
                    userLogDao.save(user.getId(),"登录成功，token生成");
                    r=ResponseMapper.setOK("ok",token );

                }else {
                    userLogDao.save(user.getId(),"登录失败，密码有误");
                    r = ResponseMapper.setERROR("密码有误");
                }
            }else  {
                userLogDao.save(user.getId(),"登录失败，账号有误");
                r = ResponseMapper.setERROR("是不是没有账号，快来注册吧");
            }
            if (r.getCode() !=1 ){
                String key = ProjectConfig.USERLOGINCOUNT+phone;
                jedisUtil.setex(key+"_"+ System.currentTimeMillis(),600,"1");
                Set<String> set = jedisUtil.keys(key+"*");
                if (set.size() == 3) {
                    userLogDao.save(user.getId(),"登录超过三次，账号锁定");
                    jedisUtil.setex(ProjectConfig.USERSD+phone,3600,"10分钟连续失败三次冻结账号");
                    r = ResponseMapper.setERROR("连续多次账号或密码错误，账号被锁定，请1小时之后再来登录");
                }
            }
        }



        return r;
    }

    @Override
    public ResponseMapper checkLogin(String token) {
        //1、校验Token有效性
        if(JwtUtil.checkJWT(token)){
            //反解析 令牌 获取当初登录的手机号
            LoginToken loginToken=JSON.parseObject(JwtUtil.parseJWT(token),LoginToken.class);
            //获取当前手机号的令牌
            String t=jedisUtil.get(ProjectConfig.TOKENPHONE+loginToken.getPhone());
            //比对令牌
            if(Objects.equals(t,token)) {
                return ResponseMapper.setOK("有效", token);
            }else {
                return ResponseMapper.setERROR("已经在其他地方登录了");
            }
        }else {
            return ResponseMapper.setERROR("Token校验失败");
        }
    }

    @Override
    public ResponseMapper exitLogin(String token) {
        if(JwtUtil.checkJWT(token)){
            jedisUtil.del(ProjectConfig.TOKENJWT+token);
            //反解析 令牌 获取当初登录的手机号
            LoginToken loginToken=JSON.parseObject(JwtUtil.parseJWT(token),LoginToken.class);
            //获取当前手机号的令牌
            jedisUtil.del(ProjectConfig.TOKENPHONE+loginToken.getPhone());

            return ResponseMapper.setOK("退出成功",null);
        }else {
            return ResponseMapper.setERROR("Token校验失败");
        }
    }
}
