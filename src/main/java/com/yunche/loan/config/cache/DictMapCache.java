package com.yunche.loan.config.cache;

import com.google.common.collect.Maps;
import com.yunche.loan.domain.entity.DictMapDO;
import com.yunche.loan.mapper.DictMapDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class DictMapCache {



    public static  Map<String, String> map = Maps.newHashMap();

    @Autowired
    DictMapDOMapper dictMapDOMapper;




    /**
     * 刷新ALL_AREA缓存
     */
    @PostConstruct
    public void refreshAll() {

        // 获取所有行政区
        List<DictMapDO> allDictMap = dictMapDOMapper.getAll();

        if (CollectionUtils.isEmpty(allDictMap)) {
            return;
        }
        allDictMap.parallelStream().forEach(e->{
            String key = e.getItemKey()+"_"+e.getSource();
            String value = e.getTarget();
            map.put(key,value);
        });

    }

   public String  getValue(String key,String source){
        if(map.containsKey(key + "_" + source)){
               return  map.get(key + "_" + source);
        }else{
            return source;
        }
   }
}
