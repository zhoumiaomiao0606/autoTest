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

    private List<UniversalCustomerFileVO> files = new ArrayList<UniversalCustomerFileVO>();

}
