package com.yunche.loan.domain.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class UniversalCustomerFileVO {
    private List<String> urls;
    private String type;
    private String name;

//    public List<String> getUrls() {
//        if(StringUtils.isBlank(urls)){
//            return null;
//        }
//        return JSON.parseArray(urls,String.class);
//    }
}
