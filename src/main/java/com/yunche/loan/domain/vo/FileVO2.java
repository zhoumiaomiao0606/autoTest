package com.yunche.loan.domain.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/26
 */
@Data
public class FileVO2 {

    private Long fileId;
    /**
     * 文件类型：
     */
    private Byte type;
    /**
     * 类型名称
     */
    private String name;

    /**
     * 文件存储路径
     */
    private String urls;

    /**
     * 过滤掉空串
     *
     * @return
     */
    public List<String> getUrls() {

        if (StringUtils.isNotBlank(urls)) {

            List<String> urlList = JSON.parseArray(urls, String.class);

            return urlList;
        }

        return Collections.EMPTY_LIST;
    }
}
