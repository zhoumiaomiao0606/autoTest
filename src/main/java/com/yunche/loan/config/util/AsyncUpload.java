package com.yunche.loan.config.util;

import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.domain.entity.BankInterfaceFileSerialDO;
import com.yunche.loan.mapper.BankInterfaceFileSerialDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AsyncUpload {

    @Resource
    private BankInterfaceFileSerialDOMapper bankInterfaceFileSerialDOMapper;

    @Async
    public void upload(String serialNo,String fileType, String name, String urls){
        //1 下载出差 2 合成出错 3 上传出错

        boolean flag = true;
        String picPath = null;
        Byte error = null;
        try {
            picPath = null;
            if(StringUtils.isBlank(picPath)){
                error = new Byte("1");
                throw new RuntimeException("文件下载出错");
            }
            boolean check = FtpUtil.icbcUpload(picPath);
            if(!check){
                error = new Byte("3");
                throw new RuntimeException("文件上传出错");
            }
        }catch (Exception e){
            flag = false;
        }

        BankInterfaceFileSerialDO bankInterfaceFileSerialDO = new BankInterfaceFileSerialDO();
        bankInterfaceFileSerialDO.setSerialNo(serialNo);
        bankInterfaceFileSerialDO.setFileName(name);
        bankInterfaceFileSerialDO.setFilePath(picPath);
        bankInterfaceFileSerialDO.setFileType(fileType);
        bankInterfaceFileSerialDO.setError(error);

        if(flag){
            bankInterfaceFileSerialDO.setSuccess(new Byte("1"));
        }else {
            bankInterfaceFileSerialDO.setSuccess(new Byte("0"));
        }
        bankInterfaceFileSerialDOMapper.insertSelective(bankInterfaceFileSerialDO);

    }

    @Async
    public void upload(String serialNo, String fileType, String name, List<String > urls){

        //1 下载出差 2 合成出错 3 上传出错
        boolean flag = true;
        String picPath = null;
        Byte error = null;

        try {
            picPath = ImageUtil.mergeImage2Pic(name,urls);
            if(StringUtils.isBlank(picPath)){
                error = new Byte("2");
                throw new RuntimeException("文件合成出错");
            }
            boolean check = FtpUtil.icbcUpload(picPath);
            if(!check){
                error = new Byte("3");
                throw new RuntimeException("文件上传出错");
            }
        }catch (Exception e){
            flag = false;
        }

        BankInterfaceFileSerialDO bankInterfaceFileSerialDO = new BankInterfaceFileSerialDO();
        bankInterfaceFileSerialDO.setSerialNo(serialNo);
        bankInterfaceFileSerialDO.setFileName(name);
        bankInterfaceFileSerialDO.setFilePath(picPath);
        bankInterfaceFileSerialDO.setFileType(fileType);
        bankInterfaceFileSerialDO.setError(error);


        if(flag){
            bankInterfaceFileSerialDO.setSuccess(new Byte("1"));
        }else {
            bankInterfaceFileSerialDO.setSuccess(new Byte("0"));
        }

        bankInterfaceFileSerialDOMapper.insertSelective(bankInterfaceFileSerialDO);

    }
}
