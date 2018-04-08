package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalCreditInfoVO {

    private String customer_cust_type;
    private String customer_name;
    private String bank_info;
    private String bank_result;
    private String process_bank_credit_result;
    private String process_bank_credit_info;
    private String process_bank_add_condition;
    private String society_info;
    private String society_result;
    private String process_society_credit_result;
    private String process_society_credit_info;
    private String process_society_add_condition;

}
