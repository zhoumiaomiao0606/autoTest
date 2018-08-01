package com.yunche.loan.domain.vo;

import lombok.Data;


import java.util.ArrayList;
import java.util.List;

@Data
public class UniversalCustomerVO {
    private String order_id;//订单编号
    private String customer_id;//主贷人id
    private String cust_type;//贷款类型
    private String name;//名字
    private String id_card;//身份证号
    private String mobile;//手机号
    private String income_certificate_company_name;
    private String income_certificate_company_address;
    private String cust_relation;//与主贷人关系 客户类型;// 1-主贷人;2-共贷人;3-担保人;4-紧急联系人;
    private String bank_result;// -1 待查询 0-不通过;1-通过;2-关注;
    private String society_result;// -1 待查询 0-不通过;1-通过;2-关注;
    private String sex;
    private String company_name;
    private String company_address;
    private String education;
    private String month_income;
    private String address;
    private String postcode;
    private String working_years;
    private String duty;
    private String guarantee_type;
    private String residence_address;//户籍地址
    private String age;//年龄
    private String guarantee_rela;
    private String customer_cprovince;
    private String customer_ccity;
    private String customer_ccounty;
    private String customer_hprovince;
    private String customer_hcity;
    private String customer_hcounty;

    private String open_card_order;//银行开卡顺序 （0：电审后  1：电审前）

    private String open_card_status;//银行开卡状态 (0:否，1：是)

    private List<UniversalCustomerFileVO> files = new ArrayList<UniversalCustomerFileVO>();

}
