package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.icbc.api.core.ApiClient;
import com.icbc.api.core.ApiException;
import com.icbc.api.core.ApiRequest;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.service.BankSolutionProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class BankSolutionProcessServiceImpl implements BankSolutionProcessService{

    @Autowired
    SysConfig sysConfig;

    @Autowired
    OSSConfig ossConfig;


    @Override
    public String  fileDownload(String filesrc) {
        String returnKey=null;
        ApiClient ac = new ApiClient(sysConfig.getPriKey());
        /*prikey:调用方私钥 ; pubKeyDir:工行API平台提供的公钥所在目录*/

        ApiRequest request = new ApiRequest(sysConfig.getApiUrl(), IDict.K_API.FILEDOWNLOAD, sysConfig.getPlatno());

        try {
            ac.doDownload(request, filesrc,sysConfig.getFileServerpath());

            String fileAndPath = FtpUtil.icbcDownload(sysConfig.getFileServerpath() + filesrc);
            OSSClient ossClient = OSSUnit.getOSSClient();
            String diskName = ossConfig.getDownLoadDiskName();
            File file = new File(fileAndPath);
            OSSUnit.uploadObject2OSS(ossClient, file, ossConfig.getBucketName(), ossConfig.getDownLoadDiskName() + File.separator);
            returnKey = diskName + File.separator + file.getName();
        } catch (ApiException e) {

            throw new BizException(e.getErrorMsg());

        }

        return returnKey;
    }
}
