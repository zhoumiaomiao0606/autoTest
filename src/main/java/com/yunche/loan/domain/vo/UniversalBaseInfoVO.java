package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/9/13
 */
@Data
public class UniversalBaseInfoVO {

    private String order_id;
    private String order_gmt_create;

    private String customer_id;
    private String customer_name;
    private String customer_id_card;
    private String customer_mobile;

    private String salesman_id;
    private String salesman_name;

    private String partner_id;
    private String partner_name;
    private String partner_code;
    private String partner_group;

    private String department_id;
    private String department_name;

    private String bank_id;
    private String bank_name;

    private String financial_id;
    private String financial_loan_amount;
    private String financial_loan_time;
    private String financial_car_price;
    private String financial_actual_car_price;

    private String car_detail_id;
    private String car_detail_name;

//    private String remit_application_date;
}
