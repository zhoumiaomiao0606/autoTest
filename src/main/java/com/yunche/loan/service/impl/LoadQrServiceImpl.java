package com.yunche.loan.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.yunche.loan.config.cache.AppVersionCache;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.QrConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.AppVersionDO;
import com.yunche.loan.service.LoadQrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static com.yunche.loan.config.constant.AppVersionConst.TERMINAL_TYPE_ANDROID;

@Service
public class LoadQrServiceImpl implements LoadQrService {
    @Autowired
    AppVersionCache appVersionCache;

    @Autowired
    OSSConfig ossConfig;

    @Override
    public void downloadQr(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws WriterException, IOException {
        AppVersionDO latestVersion = appVersionCache.getLatestVersion(TERMINAL_TYPE_ANDROID);
        String endPoint = ossConfig.getEndpoint();
        String bucketNameAndroid =ossConfig.getBucketName_android();
        String downUrl;
        if(latestVersion == null || endPoint.isEmpty()|| bucketNameAndroid.isEmpty()){
            downUrl="APP版本维护中，请稍后重试";
        }else{
            downUrl ="https://"+bucketNameAndroid+"."+endPoint+ File.separator+latestVersion.getDownloadUrl();
        }
        String dataHandle = new String(downUrl.getBytes(QrConst.UTF_8), QrConst.UTF_8);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(dataHandle, BarcodeFormat.QR_CODE, QrConst.QR_WIDTH, QrConst.QR_HEIGHT);
        httpServletResponse.reset();//清空输出流

        httpServletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        OutputStream os = httpServletResponse.getOutputStream();//取得输出流
        MatrixToImageWriter.writeToStream(bitMatrix, QrConst.QR_FILE_TYPE, os);//写入文件刷新
        os.flush();
        os.close();//关闭输出流
    }


    @Override
    public ResultBean apkDownload(){
        AppVersionDO latestVersion = appVersionCache.getLatestVersion(TERMINAL_TYPE_ANDROID);
        String endPoint = ossConfig.getEndpoint();
        String bucketNameAndroid =ossConfig.getBucketName_android();
        String downUrl;
        if(latestVersion == null || endPoint.isEmpty()|| bucketNameAndroid.isEmpty()){
            downUrl="APP版本维护中，请稍后重试";
        }else{
            downUrl ="https://"+bucketNameAndroid+"."+endPoint+ File.separator+latestVersion.getDownloadUrl();
        }
        return ResultBean.ofSuccess(downUrl);
    }
}
