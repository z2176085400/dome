package com.zkz.userloginservice.config;

import com.zkz.util.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *@Author feri
 *@Date Created in 2019/6/14 14:50
 */
@Configuration
public class IdGeneratorConfig {
    @Bean
    public IdGenerator createIG(){
        return new IdGenerator();
    }
}
