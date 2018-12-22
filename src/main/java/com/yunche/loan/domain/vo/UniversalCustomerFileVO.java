package com.yunche.loan.domain.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class UniversalCustomerFileVO {
    private String urls;
    private String type;
    private String name;

    public List<String> getUrls() {
        if (StringUtils.isBlank(urls)) {
            return new ArrayList<String>();
        }
        if (CollectionUtils.isEmpty(addurls))
        {
            return JSON.parseArray(urls, String.class);
        }
        List<String> urls1 = JSON.parseArray(urls, String.class);
        urls1.addAll(addurls);
        return urls1;

    }

    private List addurls = new ArrayList();
}
