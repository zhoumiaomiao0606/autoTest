package com.yunche.loan.config.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@PropertySource(value = "classpath:sysconfig.properties")
@Data
public class SysConfig {
    private String servierIP;//ftp 服务器IP
    private String userName;//ftp 用户名
    private String password;//ftp 密码
    private String serverpath;//ftp 远程服务器地址
    private int port;//ftp 端口
    private String platno;
    private String assurerno;
    private String tzphybrno;
    private String hzphybrno;
    private String priKey;
    private String apiUrl;
    private String fileServerpath;
    private String tempDir;
    private String serverRecvPath;
}
