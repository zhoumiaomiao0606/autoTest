package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ProductRateDO extends ProductRateDOKey {
    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Byte status;
}