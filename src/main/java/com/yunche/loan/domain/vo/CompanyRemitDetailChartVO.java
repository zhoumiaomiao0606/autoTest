package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 14:48
 * @description:
 **/
@Data
public class CompanyRemitDetailChartVO
{
   //客户编号

    //客户姓名
    private String customer_name;

    //身份证号
    private String customer_id_card;


    //贷款银行
    private String bank;

    //业务团队
    private String partner_name;

    //业务员
    private String salesman_name;

    //大区
    //省份

    //车型
    private String car_type;


    //执行利率
    private String sign_rate;

    //首付款
    private String down_payment_money;

    //贷款金额
    private String loan_amount;

    //银行分期本金
    private String bank_period_principal;

    //打款金额
    private String remit_amount;

    //创建时间
    private String gmt_create;

    //提交时间
    private String submitTime;

    //提交人
    private String username;
}
