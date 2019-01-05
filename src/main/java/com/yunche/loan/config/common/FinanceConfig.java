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

    @Value("${spring.finance.accountingVoucherHost}")
    private String accountingVoucherHost;

    @Value("${spring.finance.accountingVoucherHost}")
    private  String HOST;//财务打款单，计算，账户类

    @Value("${spring.finance.secondCarHost}")
    private String secondCarHost;//二手车

    @Value("${spring.finance.paymentHost}")
    private String paymentHost;//临时测试--金福猫支付
}
