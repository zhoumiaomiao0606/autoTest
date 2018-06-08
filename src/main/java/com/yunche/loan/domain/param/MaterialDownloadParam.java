package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class MaterialDownloadParam {

    private Long customerId;
    private String name;
    private String idCard;
    private Byte type;
    private String path;

    private List<String> pathList;

    private String typeName;

    private Byte custType;

    private String custTypeName;


    private String  fileStatus;//0-打包成功 1-打包中 2-文件不存在
}
