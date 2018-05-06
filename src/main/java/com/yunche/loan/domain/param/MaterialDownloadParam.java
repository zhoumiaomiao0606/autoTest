package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class MaterialDownloadParam {

    private String name;
    private String idCard;
    private Byte type;
    private String path;

    private List<String> pathList;

    private String typeName;

    private Byte custType;

    private String custTypeName;



}
