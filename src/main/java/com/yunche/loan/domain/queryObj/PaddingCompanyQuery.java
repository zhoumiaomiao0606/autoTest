package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Data
public class PaddingCompanyQuery extends BaseQuery {

    private String name;

    private String mnemonicCode;

    private String contact;

    private String tel;

    private String officePhone;

    private String fax;

    private String address;

    private BigDecimal businessInsuranceRebate;

    private BigDecimal trafficInsuranceRebate;

    private String bank;

    private String accountName;

    private String bankAccount;

    private String cooperationPolicy;
}
