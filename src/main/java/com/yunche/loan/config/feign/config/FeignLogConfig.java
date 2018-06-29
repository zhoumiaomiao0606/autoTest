package com.yunche.loan.config.feign.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLogConfig {


    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;

    }

}
