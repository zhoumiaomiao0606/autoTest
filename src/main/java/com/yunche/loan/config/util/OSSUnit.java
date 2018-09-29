package com.yunche.loan.config.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.yunche.loan.config.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class OSSUnit {

    // log
    private static final Logger LOG = LoggerFactory.getLogger(OSSUnit.class);

    // OSSClient
    private static final OSSClient ossClient = null;

    // 阿里云API的内或外网域名
    private static String ENDPOINT = "oss-cn-hangzhou-internal.aliyuncs.com";
    // 阿里云API的密钥Access Key ID
    private static String ACCESS_KEY_ID = "LTAIet7Gwv5j98m4";
    // 阿里云API的密钥Access Key Secret
    private static String ACCESS_KEY_SECRET = "UlIzIv8Mp30OQkhOatRufB5bBwQVRN";
    // bucketName
    public static String BUCKET_NAME = "yunche-2018";

    // init static datas
    static {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("oss");
        ENDPOINT = bundle.containsKey("endpoint") == false ? "" : bundle.getString("endpoint");
        ACCESS_KEY_ID = bundle.containsKey("accessKeyId") == false ? "" : bundle.getString("accessKeyId");
        ACCESS_KEY_SECRET = bundle.containsKey("accessKeySecret") == false ? "" : bundle.getString("accessKeySecret");
        BUCKET_NAME = bundle.containsKey("bucketName") == false ? "" : bundle.getString("bucketName");
    }

    /**
     * 获取阿里云OSS客户端对象
     */
    public static final OSSClient getOSSClient() {

        if (null == ossClient) {
            return new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        }

        return ossClient;
    }

    public static final OSSObject getObject(OSSClient ossClient, String key) {

        return ossClient.getObject(BUCKET_NAME, key);

    }

    /**
     * 新建Bucket  --Bucket权限:私有
     *
     * @param bucketName bucket名称
     * @return true 新建Bucket成功
     */
    public static final boolean createBucket(OSSClient client, String bucketName) {
        Bucket bucket = client.createBucket(bucketName);
        return bucketName.equals(bucket.getName());
    }

    /**
     * 删除Bucket
     *
     * @param bucketName bucket名称
     */
    public static final void deleteBucket(OSSClient client, String bucketName) {
        client.deleteBucket(bucketName);
        LOG.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 向阿里云的OSS存储中存储文件  --file也可以用InputStream替代
     *
     * @param client     OSS客户端
     * @param file       上传文件
     * @param bucketName bucket名称
     * @param diskName   上传文件的目录  --bucket下文件的路径
     * @return String    唯一MD5数字签名
     */
    public static final String uploadObject2OSS(OSSClient client, File file, String bucketName, String diskName) {
        String resultStr = null;
        try {
            InputStream is = new FileInputStream(file);
            String fileName = file.getName();
            Long fileSize = file.length();
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("attachment;filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件
            PutObjectResult putResult = client.putObject(bucketName, diskName + fileName, is, metadata);
            //解析结果
            resultStr = putResult.getETag();
        } catch (Exception e) {
            LOG.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
            throw new BizException("上传阿里云OSS服务器异常." + e.getMessage());
        }
        return resultStr;
    }


    /**
     * 根据key获取OSS服务器上的文件输入流
     *
     * @param client     OSS客户端
     * @param bucketName bucket名称
     * @param key        Bucket下的文件的路径名+文件名
     */
    public static final InputStream getOSS2InputStream(OSSClient client, String bucketName, String key) {
        OSSObject ossObj = client.getObject(bucketName, key);
        return ossObj.getObjectContent();
    }

    /**
     * 根据key获取文件流
     *
     * @param key
     * @return
     */
    public static final InputStream getOSS2InputStream(String key) {
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        return ossClient.getObject(BUCKET_NAME, key).getObjectContent();

    }

    /**
     * 根据key删除OSS服务器上的文件
     *
     * @param client     OSS客户端
     * @param bucketName bucket名称
     * @param diskName   文件路径
     * @param key        Bucket下的文件的路径名+文件名
     */
    public static void deleteFile(OSSClient client, String bucketName, String diskName, String key) {
        client.deleteObject(bucketName, diskName + key);
        LOG.info("删除" + bucketName + "下的文件" + diskName + key + "成功");
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public static final String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        } else if ("gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        } else if ("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension) || "png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        } else if ("html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        } else if ("txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        } else if ("vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        } else if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        } else if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        } else if ("xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        } else if ("zip".equalsIgnoreCase(fileExtension)) {
            return "application/zip";
        } else if ("xlsx".equalsIgnoreCase(fileExtension)) {
            return "application/vndopenxmlformats-officedocumentspreadsheetmlsheet";
        }

        return "text/html";
    }

}
