package com.yunche.loan.config.common;

import com.genxiaogu.ratelimiter.advice.MethodRateLimiterBeforeInterceptor;
import com.genxiaogu.ratelimiter.configuration.LimiterConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuzhe
 * @date 2018/4/16
 */
//@Configuration
public class LimitConfig2 extends LimiterConfiguration {


    @Override
    @Bean
    public MethodRateLimiterBeforeInterceptor methodAroundInterceptor() {
        return new LimitConfig();
    }

}
