package com.zkz.userloginservice.dao;

import com.zkz.entity.User;
import org.apache.ibatis.annotations.Select;

/**
 *@Author feri
 *@Date Created in 2019/6/14 14:26
 */
public interface UserDao {
    @Select("select * from user where flag=1 and phone=#{phone}")
    User selectByPhone(String phone);


}
