package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.client.ICBCFeignFileDownLoad;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.ViolationUtil;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.param.ICBCApiCallbackParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.service.BankSolutionProcessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
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
    ICBCFeignFileDownLoad icbcFeignFileDownLoad;

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Resource
    private ViolationUtil violationUtil;



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

    @Override
    public void applyCreditCallback(ICBCApiCallbackParam.ApplyCreditCallback applyCreditCallback) {
        violationUtil.violation(applyCreditCallback);
        //只有在非成功状态和退回的流水可以进行更新
        if(!checkStatus(applyCreditCallback.getPub().getCmpseq())){
            return;
        }


        if(sysConfig.getAssurerno().equals(applyCreditCallback.getPub().getAssurerno())){
            throw new BizException("保单号错误");
        }

        if(sysConfig.getPlatno().equals(applyCreditCallback.getPub().getPlatno())){
            throw new BizException("平台编号错误");
        }
        /*
        001:通过；
        003:不通过；
        099:退回，由于资料不全等原因退回
        */
        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(applyCreditCallback.getPub().getCmpseq());
        if(IDict.K_RESULT.PASS.equals(applyCreditCallback.getReq().getResult())){
            bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.SUCCESS));
        }else if(IDict.K_RESULT.NOPASS.equals(applyCreditCallback.getReq().getResult())){
            bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
            bankInterfaceSerialDO.setRejectReason(applyCreditCallback.getReq().getNote());
        }else if(IDict.K_RESULT.BACK.equals(applyCreditCallback.getReq().getResult())){
            bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
            bankInterfaceSerialDO.setRejectReason(applyCreditCallback.getReq().getNote());
        }else{
            throw new BizException("未知错误");
        }
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
    }

    @Override
    public void applyDiviGeneralCallback(ICBCApiCallbackParam.ApplyDiviGeneralCallback applyDiviGeneralCallback) {
        violationUtil.violation(applyDiviGeneralCallback);
        if(!checkStatus(applyDiviGeneralCallback.getPub().getCmpseq())){
            return;
        }


        if(sysConfig.getAssurerno().equals(applyDiviGeneralCallback.getPub().getAssurerno())){
            throw new BizException("保单号错误");
        }

        if(sysConfig.getPlatno().equals(applyDiviGeneralCallback.getPub().getPlatno())){
            throw new BizException("平台编号错误");
        }


        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(applyDiviGeneralCallback.getPub().getCmpseq());
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
        bankInterfaceSerialDO.setRejectReason(applyDiviGeneralCallback.getReq().getBacknote());
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
    }

    @Override
    public void multimediaUploadCallback(ICBCApiCallbackParam.MultimediaUploadCallback multimediaUploadCallback) {
        violationUtil.violation(multimediaUploadCallback);
        if(!checkStatus(multimediaUploadCallback.getPub().getCmpseq())){
            return;
        }

        if(sysConfig.getAssurerno().equals(multimediaUploadCallback.getPub().getAssurerno())){
            throw new BizException("保单号错误");
        }

        if(sysConfig.getPlatno().equals(multimediaUploadCallback.getPub().getPlatno())){
            throw new BizException("平台编号错误");
        }

        BankInterfaceSerialDO bankInterfaceSerialDO = new BankInterfaceSerialDO();
        bankInterfaceSerialDO.setSerialNo(multimediaUploadCallback.getPub().getCmpseq());
        bankInterfaceSerialDO.setStatus(new Byte(IDict.K_JYZT.BACK));
        bankInterfaceSerialDO.setRejectReason(multimediaUploadCallback.getReq().getBacknote());
        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
    }

    private boolean checkStatus(String cmpseq){
        if(StringUtils.isBlank(cmpseq)){
            throw new BizException("缺少流水号");
        }
        BankInterfaceSerialDO V = bankInterfaceSerialDOMapper.selectByPrimaryKey(cmpseq);
        if(V.getApiStatus() == null){
            return false;
        }
        if(V.getApiStatus().intValue() == 200){
            return false;
        }

        //只有处理中和超时状态才能进行后续处理
        if(!IDict.K_JYZT.PROCESS.equals(V.getStatus()) && !IDict.K_JYZT.TIMEOUT.equals(V.getStatus())){
            return false;
        }

        return true;
    }
}
