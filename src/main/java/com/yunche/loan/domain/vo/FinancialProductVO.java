package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FinancialProductVO {
    private Long prodId;

    private String prodName;

    private String bankName;

    private String mnemonicCode;

    private String account;

    private String signPhone;

    private String signBankCode;

    private Integer bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Integer mortgageTerm;

    private Long areaId;

    private String prov;

    private Long provId;

    private String city;

    private Long cityId;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;
}