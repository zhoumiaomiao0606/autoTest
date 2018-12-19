package com.yunche.loan.config.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
//@PropertySource(value = "classpath:financeConfig.properties")
@Data
public class FinanceConfig {
    @Value("${spring.finance.callBackUrl}")
    private String callBackUrl;
}
