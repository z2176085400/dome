package com.zkz.message.core.dao;

import com.zkz.message.core.entity.MsgReceive;

import java.util.List;

public interface MsgReceiveMapper {

    int insert(MsgReceive record);

    List<MsgReceive> selectAll();
}