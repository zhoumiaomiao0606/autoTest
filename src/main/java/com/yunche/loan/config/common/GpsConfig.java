package com.yunche.loan.config.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@PropertySource(value = "classpath:gpsconfig.properties")
@Data
public class GpsConfig {
    private String jimiUrl;
    private String jimiKey;
    private String jimiSecret;
    private String carLoanUrl;
    private String carLoanKey;

}
