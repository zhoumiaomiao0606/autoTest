package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;

@Data
public class CarBrandDO {
    private Long id;

    private String name;

    private String initial;

    private String logo;

    private Date gmtCreate;

    private Date gmtModify;
}