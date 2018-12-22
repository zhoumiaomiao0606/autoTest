package com.yunche.loan.config.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@PropertySource(value = "classpath:jtxftpconfig.properties")
@Data
public class JtxConfig {
    @Value("${spring.jtx.jtxServierIP}")
    private String jtxServierIP;
    @Value("${spring.jtx.jtxUserName}")
    private String jtxUserName;
    @Value("${spring.jtx.jtxPassword}")
    private String jtxPassword;
    @Value("${spring.jtx.jtxUrl}")
    private String jtxUrl;
    @Value("${spring.jtx.jtxPort}")
    private int jtxPort;
    private String jtxTempDirRes;
    private String jtxTempDirSend;
}
