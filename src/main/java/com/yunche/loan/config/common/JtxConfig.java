package com.yunche.loan.config.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@PropertySource(value = "classpath:jtxftpconfig.properties")
@Data
public class JtxConfig {
    private String jtxServierIP;
    private String jtxUserName;
    private String jtxPassword;
    private String jtxUrl;
    private int jtxPort;
    private String jtxTempDirRes;
    private String jtxTempDirSend;
}
