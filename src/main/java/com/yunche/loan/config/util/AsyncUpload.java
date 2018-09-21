package com.yunche.loan.config.util;

import com.google.common.collect.Sets;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.domain.entity.BankInterfaceFileSerialDO;
import com.yunche.loan.mapper.BankInterfaceFileSerialDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


@Component
public class AsyncUpload {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncUpload.class);

    private static final Set<String> PIC_SUFFIX = Sets.newHashSet("jpg", "png");

    private static final Set<String> DOCX_SUFFIX = Sets.newHashSet("docx","doc");

    @Resource
    private BankInterfaceFileSerialDOMapper bankInterfaceFileSerialDOMapper;

    @Resource
    private ViolationUtil violationUtil;

    @Resource
    private SysConfig sysConfig;

    @Resource
    private ICBCFeignClient icbcFeignClient;


    @Async
    public void execute(Process process){
        process.process();
    }

    public void upload(String serNo,List<ICBCApiRequest.PicQueue> queue){
        for(ICBCApiRequest.PicQueue picQueue :queue){

            //1 下载出错 2 合成出错 3 上传出错
            boolean flag = true;
            String picPath = null;
            Byte error = null;
            try {
                try {
                    picPath = ImageUtil.getSingleFile(picQueue.getPicName(),picQueue.getUrl(),picQueue.getPicId());
                    if(StringUtils.isBlank(picPath)){
                        error = new Byte("1");
                        throw new RuntimeException("文件下载出错");
                    }
                }catch (Exception e){
                    throw new RuntimeException("文件下载出错");
                }

                try {
                    LOG.info("文件上传开始.....");
                    boolean check = FtpUtil.icbcUpload(picPath);
                    if(!check){
                        error = new Byte("3");
                        throw new RuntimeException("文件上传出错");
                    }
                    LOG.info("文件上传成功.....");
                }catch (Exception e){
                    throw new RuntimeException("文件上传出错");
                }
            }catch (Exception e){
                flag = false;
            }

            BankInterfaceFileSerialDO bankInterfaceFileSerialDO = new BankInterfaceFileSerialDO();
            bankInterfaceFileSerialDO.setSerialNo(serNo);
            bankInterfaceFileSerialDO.setFileName(picQueue.getPicName());
            bankInterfaceFileSerialDO.setFilePath(picPath);
            bankInterfaceFileSerialDO.setFileType(picQueue.getPicId());
            bankInterfaceFileSerialDO.setError(error);

            if(flag){
                bankInterfaceFileSerialDO.setSuccess(new Byte("1"));
            }else {
                bankInterfaceFileSerialDO.setSuccess(new Byte("0"));
            }
            bankInterfaceFileSerialDOMapper.insertSelective(bankInterfaceFileSerialDO);

        }

    }



    public void upload(String serialNo,String fileType, String name, String urls){
        //1 下载出差 2 合成出错 3 上传出错

        boolean flag = true;
        String picPath = null;
        Byte error = null;
        try {
            try {
                picPath = ImageUtil.getSingleFile(name,urls,fileType);
                if(StringUtils.isBlank(picPath)){
                    error = new Byte("1");
                    throw new RuntimeException("文件下载出错");
                }
            }catch (Exception e){
                throw new RuntimeException("文件下载出错");
            }

            try {
                boolean check = FtpUtil.icbcUpload(picPath);
                if(!check){
                    error = new Byte("3");
                    throw new RuntimeException("文件上传出错");
                }
                LOG.info("文件上传成功.....");
            }catch (Exception e){
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

    public void upload(String serialNo, String fileType, String name, List<String > urls){

        //1 下载出差 2 合成出错 3 上传出错
        boolean flag = true;
        String picPath = null;
        Byte error = null;

        try {
            try {
                String suffix = name.split("\\.")[1].trim();
                if(PIC_SUFFIX.contains(suffix)){
                    picPath = ImageUtil.mergeImage2Pic(name,urls);
                }else if(DOCX_SUFFIX.contains(suffix)){
                    picPath = ImageUtil.mergeImage2Doc(name,urls);
                }else{
                    throw new RuntimeException("文件后缀有误");
                }

                if(StringUtils.isBlank(picPath)){
                    error = new Byte("2");
                    throw new RuntimeException("文件合成出错");
                }
            }catch (Exception e){
                throw new RuntimeException("文件合成出错");
            }

            try {
                boolean check = FtpUtil.icbcUpload(picPath);
                if(!check){
                    error = new Byte("3");
                    throw new RuntimeException("文件上传出错");
                }
                LOG.info("文件上传成功.....");
            }catch (Exception e){
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
