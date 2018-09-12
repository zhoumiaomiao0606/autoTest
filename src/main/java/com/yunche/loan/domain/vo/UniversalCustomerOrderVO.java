package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalCustomerOrderVO {

    private String order_id;
    private String customer_name;
    private String customer_id_card;

    private String customer_id;
    private String customer_mobile;
    private String salesman_id;
    private String salesman_name;
    private String department_id;
    private String department_name;
    private String partner_id;
    private String partner_name;
    private String bank_name;
    private String order_gmt_create;

    // 打款金额
    private String remit_amount;
    // 打款时间
    private String remit_time;
}
