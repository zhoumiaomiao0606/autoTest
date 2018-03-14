package com.yunche.loan.domain.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class UniversalCustomerFileVO {
    private String urls;
    private String type;

    public List<String> getUrls() {
        if(StringUtils.isBlank(urls)){
            return null;
        }
        return JSON.parseArray(urls,String.class);
    }
}
