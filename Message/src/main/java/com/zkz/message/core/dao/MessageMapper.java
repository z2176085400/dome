package com.zkz.message.core.dao;

import com.zkz.message.core.entity.Message;

import java.util.List;

public interface MessageMapper {

    int insert(Message record);

    List<Message> selectAll();
}