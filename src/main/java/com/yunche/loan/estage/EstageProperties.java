package com.yunche.loan.estage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * author: yu.hb
 * Date: 2019-03-07
 */
@Configuration
@ConfigurationProperties(prefix = "spring.estage")
@Data
public class EstageProperties {

    private String assurerNo;

    private String platNo;

    private String bankCode;

    private String productType;

    private String publicKey;

    private String signPrivateKey;

    private String systemUrl;
}
