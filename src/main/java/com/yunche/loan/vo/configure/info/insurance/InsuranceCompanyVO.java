package com.yunche.loan.vo.configure.info.insurance;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Data
public class InsuranceCompanyVO {
    private Integer id;

    private String name;

    private String mnemonicCode;

    private String contact;

    private String tel;

    private String officePhone;

    private String fax;

    private String address;

    private Double businessInsuranceRebate;

    private Double trafficInsuranceRebate;

    private String bank;

    private String accountName;

    private String bankAccount;

    private String file;

    private String cooperationPolicy;
}
