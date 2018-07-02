package com.yunche.loan.config.task;

import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.entity.MaterialDownHisDO;
import com.yunche.loan.mapper.MaterialDownHisDOMapper;
import com.yunche.loan.service.BankOpenCardService;
import com.yunche.loan.service.BankSolutionProcessService;
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
    @Scheduled(cron = "0 0/10 0 * * ?")
    public void deleteOverdueFile(){
        List<MaterialDownHisDO> all = Lists.newArrayList();
        List<MaterialDownHisDO> materialDownHisSUCC = materialDownHisDOMapper.listByStatus(IDict.K_JYZT.PRE_TRANSACTION);
        List<MaterialDownHisDO> materialDownHisFAIL = materialDownHisDOMapper.listByStatus(IDict.K_JYZT.FAIL);
        all.addAll(materialDownHisSUCC);
        all.addAll(materialDownHisFAIL);
        if(!CollectionUtils.isEmpty(all)){
            all.parallelStream().forEach(e->{
                LOG.info(e.getFileName()+"文件下载开始");
                //先锁定记录为处理中
                e.setStatus(IDict.K_JYZT.PROCESS);
                materialDownHisDOMapper.updateByPrimaryKeySelective(e);

                //文件下载
                String  key=null;
                if(StringUtil.isEmpty(e.getFileKey())){
                    key = bankSolutionProcessService.fileDownload(e.getFileName());
                    if(StringUtil.isEmpty(key)){
                        e.setStatus(IDict.K_JYZT.FAIL);
                        e.setInfo("文件下载失败");
                        materialDownHisDOMapper.updateByPrimaryKeySelective(e);
                        LOG.info(e.getFileName()+":文件下载失败");
                        return;
                    }
                }else{
                    key = e.getFileKey();
                }

                LOG.info(e.getFileName()+":文件下载完成");
                LOG.info(e.getFileName()+":文件导入开始");
                boolean b = bankOpenCardService.importFile(key);
                if(b){
                    e.setStatus(IDict.K_JYZT.SUCCESS);
                    e.setInfo("文件导入成功");
                    materialDownHisDOMapper.updateByPrimaryKeySelective(e);
                    LOG.info(e.getFileName()+":文件导入完成,key:"+key);
                }else {
                    e.setStatus(IDict.K_JYZT.FAIL);
                    e.setInfo("文件导入失败");
                    materialDownHisDOMapper.updateByPrimaryKeySelective(e);
                    LOG.info(e.getFileName()+":文件导入失败,key:"+key);
                }
            });
        }
    }
}
