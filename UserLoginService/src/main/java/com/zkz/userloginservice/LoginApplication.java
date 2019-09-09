package com.zkz.userloginservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 *@Author feri
 *@Date Created in 2019/6/14 14:25
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.zkz.userloginservice.dao")
public class LoginApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class,args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(LoginApplication.class);
    }

}
