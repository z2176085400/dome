package com.zkz.userloginservice.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 *@Author feri
 *@Date Created in 2019/6/14 14:26
 */
public interface UserLogDao {
    @Insert("insert into userlog(uid,flag,content,createtime) values(#{uid},2,#{content},now())")
    int save(@Param("uid") int uid, @Param("content") String content);

}
