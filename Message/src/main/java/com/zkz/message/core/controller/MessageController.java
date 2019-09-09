package com.zkz.message.core.controller;


import com.zkz.message.core.entity.Message;
import com.zkz.message.service.MessageService;
import com.zkz.model.EmailMsg;
import com.zkz.responsemapper.ResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *@Author feri
 *@Date Created in 2019/6/13 13:10
 */
@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;
    @GetMapping("/message/page.do")
    public ResponseMapper all(int page, int count){
        return messageService.page(page, count);
    }
    @PostMapping("/message/sendmsg.do")
    public ResponseMapper sendMsg(@RequestBody Message message, HttpServletRequest request){
        return messageService.sendMessage(message,request.getRemoteAddr());
    }
    @GetMapping("/message/checkcode.do")
    public ResponseMapper check(String phone,int code){


        return messageService.checkCode(phone, code);
    }
    @GetMapping("/message/checkcodeEmail.do")
    public ResponseMapper checkEmail(@RequestParam("data") String data,@RequestParam("email") String  email){

        return messageService.checkEmail(data,email);
    }
}