package com.zkz.message.service;

import com.zkz.message.core.entity.Message;
import com.zkz.model.EmailMsg;
import com.zkz.responsemapper.ResponseMapper;

public interface MessageService {

    ResponseMapper page(int page,int count);
    ResponseMapper checkCode(String phone,int code);
    ResponseMapper sendMessage(Message message,String ip);
    ResponseMapper checkEmail(String data, String  email);
}
