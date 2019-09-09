package com.zkz.message.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkz.config.ProjectConfig;
import com.zkz.message.core.dao.MessageLogMapper;
import com.zkz.message.core.dao.MessageMapper;
import com.zkz.message.core.dao.MsgReceiveMapper;
import com.zkz.message.core.entity.Message;
import com.zkz.message.core.entity.MessageLog;
import com.zkz.message.core.entity.MsgReceive;
import com.zkz.message.service.MessageService;
import com.zkz.model.ActiveCode;
import com.zkz.model.EmailMsg;
import com.zkz.responsemapper.ResponseMapper;
import com.zkz.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageLogMapper messageLogMapper;
    @Autowired
    private MsgReceiveMapper msgReceiveMapper;
    @Autowired
    private JedisUtil jedisUtil;


    @Override
    public ResponseMapper page(int page, int count) {
        PageHelper.startPage(page, count);
        PageInfo<Message> pageInfo = new PageInfo<>(messageMapper.selectAll());

        return ResponseMapper.setOK("ok",pageInfo);
    }

    @Override
    public ResponseMapper checkCode(String phone, int code) {
        String key = ProjectConfig.SMSCODE+phone;
        if (jedisUtil.exists(key)){
            String vcode = jedisUtil.get(key);
            if (vcode != null){
                if(Integer.parseInt(vcode)==code) {
                    return ResponseMapper.setOK("验证码正确", null);
                }else {
                    return ResponseMapper.setERROR("验证码错误，请重新输入");
                }
            }
        }
        return ResponseMapper.setERROR("验证码过期，请重新获取验证码");
    }

    @Override
    public ResponseMapper sendMessage(Message message, String ip) {
        ResponseMapper r;
        if(message.getType()<4){
             r =sendMsm(message,ip);

        }else {
            //邮箱
        r=sendEmial(message,ip);


        }

        return r;
    }



    private ResponseMapper sendMsm(Message message,String ip) {
        int count = 0;
        String c = jedisUtil.get(ProjectConfig.SMSPREDAY + message.getReceive());
        if (c != null && c.matches("[0-9]{1,2}")) {
            count = Integer.parseInt(c);
            if (count >= 20) {
                return ResponseMapper.setERROR("亲，一天只能 发送20条短信息哦");

            }
            if (jedisUtil.exists(ProjectConfig.SMSPRELIMIT + message.getReceive())) {
                return ResponseMapper.setERROR("一个 手机号一分钟只能发送一次短信");
            }
        }
        int code = CodeUtil.createNum(6);
        int flag =  3;
        String info = "";
        boolean isfalg = false;
        if(jedisUtil.exists(ProjectConfig.SMSCODE+message.getReceive())){
            //上次的验证码还没失效
            code=Integer.parseInt(jedisUtil.get(ProjectConfig.SMSCODE+message.getReceive()));
        }else {
            code = CodeUtil.createNum(6);
            isfalg=true;
        }
        info="发送短信验证码："+code;
        //验证码 三分钟有效
        SmsUtil.mobileQuery(message.getReceive(),code);
        flag=1;//发送成功
        //1分钟
        jedisUtil.setex(ProjectConfig.SMSPRELIMIT+message.getReceive(),60,"短信发送限制");
        //1天
        jedisUtil.setex(ProjectConfig.SMSPREDAY+message.getReceive(), TimeUtil.getLastSeconds(),(count+1)+"");


        if (isfalg) {
            String s3 = jedisUtil.setex(ProjectConfig.SMSCODE + message.getReceive(), 600, code + "");

        }
        save(message,info,ip);
        return ResponseMapper.setOK();

    }
    private ResponseMapper sendEmial(Message message,String ip){
        String info;
        int code=CodeUtil.createNum(6);
        int flag=2;
        info="发送邮箱激活码："+code;
        EmailMsg emailMsg=new EmailMsg();
        emailMsg.setCompany(ProjectConfig.projects.get(message.getPcode()));
        ActiveCode code1=new ActiveCode();
        code1.setEmail(message.getReceive());
        code1.setCode(code);


        String mw= EncryptionUtil.AESEnc(ProjectConfig.AESKEYACTIVECODE, JSON.toJSONString(code));
        String u=ProjectConfig.ACTIVEURL+"?data="+mw+"&email="+code1.getEmail()+"&aaaaa=zhkzlhvkslkn";
        /* emailMsg.setContent("欢迎注册："+emailMsg.getCompany()+",请激活使用，<a href=''></a>");*/
        jedisUtil.setex(ProjectConfig.EMAILCODE+code1.getEmail(),60*60*24,code+"");
        //1天

        emailMsg.setContent("欢迎注册："+emailMsg.getCompany()+",<a href='"+u+"'>激活请点击</a>");
        emailMsg.setEmail(message.getReceive());
        EmailUtil.sendEmail(emailMsg);
        flag=1;
        save(message,info,ip);
        return ResponseMapper.setOK("OK",null);


    }
    private void save(Message message,String info,String ip){
        //记录消息发送信息
        messageMapper.insert(message);
        //记录日志
        MessageLog log=new MessageLog();
        log.setMid(message.getId());
        log.setInfo(info);
        log.setIp(ip);
        messageLogMapper.insert(log);
        //记录收件人
        MsgReceive receive=new MsgReceive();
        receive.setNo(message.getReceive());
        receive.setFlag(message.getType()<4?1:2);
        msgReceiveMapper.insert(receive);
    }
/*邮箱验证时加密不同*/
    /*
    * @param data url连接的key ，email 接收邮箱地址，老邢对邮箱进行加密了。我解加密不了，故没加密
    *
    * */
    @Override
    public ResponseMapper checkEmail(String data,String email) {
/*
* 获取URL连接是否被点击过
* */
        String key = ProjectConfig.EMAILCODE+ email;
            //设置连接失效，即点击后失效
        if (jedisUtil.exists(key)){
            String vcode = jedisUtil.get(key);

            if (jedisUtil.exists(ProjectConfig.EMAILUSE+vcode)){
                return ResponseMapper.setERROR("验证码已经使用过了，请重新获取验证码");
            }

            if (vcode != null){
                //把rides中的验证码拿出来进行加密，和传过来的data值进行比较
                if(EncryptionUtil.AESEnc(ProjectConfig.AESKEYACTIVECODE,vcode).equals(data)) {
                   String s= EncryptionUtil.AESEnc(ProjectConfig.AESKEYACTIVECODE,vcode);
                   //设置连接失效
                    jedisUtil.setnx(ProjectConfig.EMAILUSE+vcode,vcode);
                    return ResponseMapper.setOK("验证码正确", s);
                }else {
                    //设置连接失效
                    jedisUtil.setnx(ProjectConfig.EMAILUSE+vcode,vcode);
                    return ResponseMapper.setERROR("验证码错误，重新输入");
                }
            }
        }
        return ResponseMapper.setERROR("验证码过期，请重新获取验证码");

    }
}
