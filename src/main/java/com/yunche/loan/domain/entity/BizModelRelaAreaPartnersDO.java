package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BizModelRelaAreaPartnersDO {

    private Long bizId;

    private Long areaId;

    private Long groupId;

    private String prov;

    private String city;

    private Date gmtCreate;

    private Date gmtModify;
}