package com.zkz.message.core.entity;

import lombok.Data;

import java.util.Date;
@Data
public class MessageLog {
    private Long id;

    private long mid;

    private Date createtime;

    private String ip;

    private String info;


}