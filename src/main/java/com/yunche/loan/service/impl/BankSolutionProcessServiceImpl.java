package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.client.ICBCFeignNormal;
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

    @Autowired
    ICBCFeignClient icbcFeignClient;

    @Autowired
    ICBCFeignNormal icbcFeignFileDownLoad;

    @Override
    public String  fileDownload(String filesrc) {

        String returnKey=null;
        try {
            boolean filedownload = icbcFeignFileDownLoad.filedownload(filesrc);
            String fileAndPath = FtpUtil.icbcDownload(sysConfig.getFileServerpath() + filesrc);
            OSSClient ossClient = OSSUnit.getOSSClient();
            String diskName = ossConfig.getDownLoadDiskName();
            File file = new File(fileAndPath);
            OSSUnit.deleteFile(ossClient,ossConfig.getBucketName(),ossConfig.getDownLoadDiskName()+File.separator,file.getName());
            OSSUnit.uploadObject2OSS(ossClient, file, ossConfig.getBucketName(), ossConfig.getDownLoadDiskName() + File.separator);
            returnKey = diskName + File.separator + file.getName();
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

        return returnKey;
    }
}
