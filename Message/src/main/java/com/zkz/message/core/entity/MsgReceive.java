package com.zkz.message.core.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class MsgReceive {
    private Long id;

    private String no;
    @DateTimeFormat
    private Date createtime;

    private Integer flag;


}