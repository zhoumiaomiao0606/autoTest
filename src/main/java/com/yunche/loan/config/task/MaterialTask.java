package com.yunche.loan.config.task;

import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.entity.MaterialDownHisDO;
import com.yunche.loan.mapper.MaterialDownHisDOMapper;
import com.yunche.loan.service.BankOpenCardService;
import com.yunche.loan.service.BankRepayRecordService;
import com.yunche.loan.service.BankSolutionProcessService;
import com.yunche.loan.service.UnsecuredService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MaterialTask {
    private static final Logger LOG = LoggerFactory.getLogger(OSSUnit.class);
    @Autowired
    MaterialDownHisDOMapper materialDownHisDOMapper;

    @Autowired
    BankSolutionProcessService bankSolutionProcessService;

    @Autowired
    BankOpenCardService bankOpenCardService;

    @Autowired
    BankRepayRecordService bankRepayRecordService;

    @Autowired
    UnsecuredService unsecuredService;
    @Scheduled(cron = "0 0/1 * * * ?")
    public void filedownload(){
        List<MaterialDownHisDO> all = Lists.newArrayList();
        List<MaterialDownHisDO> materialDownHisSUCC = materialDownHisDOMapper.listByStatus(IDict.K_JYZT.PRE_TRANSACTION);
        List<MaterialDownHisDO> materialDownHisFAIL = materialDownHisDOMapper.listByStatus(IDict.K_JYZT.FAIL);
        all.addAll(materialDownHisSUCC);
        all.addAll(materialDownHisFAIL);

            if(!CollectionUtils.isEmpty(all)){
                all.stream().forEach(e->{
                    try{
                        LOG.info(e.getFileName()+"文件下载开始");
                        //先锁定记录为处理中
                        e.setStatus(IDict.K_JYZT.PROCESS);
                        materialDownHisDOMapper.updateByPrimaryKeySelective(e);
                        //文件下载
                        String  key=null;
                        if(StringUtil.isEmpty(e.getFileKey())){
                            key = bankSolutionProcessService.fileDownload(e.getFileName());
                            if(StringUtil.isEmpty(key)){
                               throw new BizException("文件下载失败");
                            }else{
                                e.setFileKey(key);
                                materialDownHisDOMapper.updateByPrimaryKeySelective(e);
                            }
                        }else{
                            key = e.getFileKey();
                        }

                        LOG.info(e.getFileName()+":文件下载完成");
                        LOG.info(e.getFileName()+":文件导入开始");
                        switch(e.getFileType()){
                            case IDict.K_WJLX.WJLX_0:
                                boolean b0 = bankOpenCardService.importFile(key);
                                afterAction(b0,e);
                                break;
                            case IDict.K_WJLX.WJLX_1:
                                boolean b1 = bankRepayRecordService.autoImportFile(key);
                                afterAction(b1,e);
                                break;
                            case IDict.K_WJLX.WJLX_2:
                                boolean b2 = unsecuredService.autoUnsecuredImp(key);
                                afterAction(b2,e);
                                break;
                            case IDict.K_WJLX.WJLX_3:break;
                            default:
                                break;
                        }
                    }catch(Exception e2){
                        LOG.info(e2.getMessage());
                        modifyFail(e);
                    }
                });
            }
    }

    public void afterAction(boolean b,MaterialDownHisDO e){
        if(b){
            modifySucc(e);
        }else{
            modifyFail(e);
        }

    }


    public void modifySucc(MaterialDownHisDO e){
        e.setStatus(IDict.K_JYZT.SUCCESS);
        e.setInfo("文件处理成功");
        materialDownHisDOMapper.updateByPrimaryKeySelective(e);
        LOG.info(e.getFileName()+":文件处理完成,key:"+e.getFileKey());
    }
    public void modifyFail(MaterialDownHisDO e){
        e.setStatus((byte)6);
        e.setInfo("文件处理失败");
        materialDownHisDOMapper.updateByPrimaryKeySelective(e);
        LOG.info(e.getFileName()+":文件处理失败,key:"+e.getFileKey());
    }
}
