package com.yunche.loan.config.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@PropertySource(value = "classpath:oss.properties")
@Data
public class OSSConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String bucketName_android;
    private String zipBucketName;
    private String downLoadBasepath;
    private String zipDiskName;
    private String down2tomcatBasepath;
    private String downLoadDiskName;
    private String videoBucketName;
}
