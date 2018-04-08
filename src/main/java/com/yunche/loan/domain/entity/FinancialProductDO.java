package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FinancialProductDO {

    private Long prodId;

    private String prodName;

    private String bankName;

    private String mnemonicCode;

    private String account;

    private String signPhone;

    private String signBankCode;

    private Byte bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Byte mortgageTerm;

    private Long areaId;

    private String prov;

    private String city;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private Byte status;

    private Integer formulaId;
}