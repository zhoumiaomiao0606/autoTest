package com.yunche.loan.config.task;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.domain.entity.MaterialDownHisDO;
import com.yunche.loan.mapper.MaterialDownHisDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class MaterialTask {
    SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    @Autowired
    MaterialDownHisDOMapper materialDownHisDOMapper;

    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteOverdueFile(){

        List<MaterialDownHisDO> materialDownHisDOS = materialDownHisDOMapper.listAll();

        if(!CollectionUtils.isEmpty(materialDownHisDOS)){
            materialDownHisDOS.stream().forEach(e->{
                long  day=(new Date().getTime()-e.getGmtCreate().getTime())/(24*60*60*1000);
                if(day>=1){
                    File file = new File(e.getUrl());
                    if(file!=null){
                        file.delete();
                    }
                    e.setStatus(BaseConst.DEL_STATUS);
                    int count = materialDownHisDOMapper.updateByPrimaryKeySelective(e);
                    Preconditions.checkArgument(count>0,"删除数据出错");
                }
            });
        }
    }
}
