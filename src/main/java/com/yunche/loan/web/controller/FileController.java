package com.yunche.loan.web.controller;

import com.aliyun.oss.OSSClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileController {





    private void oss() throws FileNotFoundException {
        // endpoint以杭州为例，其它region请按实际情况填写
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建
        String accessKeyId = "<yourAccessKeyId>";
        String accessKeySecret = "<yourAccessKeySecret>";
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // 上传文件流
        InputStream inputStream = new FileInputStream("localFile");
        ossClient.putObject("<yourBucketName>", "<yourKey>", inputStream);
        // 关闭client
        ossClient.shutdown();
    }
}
