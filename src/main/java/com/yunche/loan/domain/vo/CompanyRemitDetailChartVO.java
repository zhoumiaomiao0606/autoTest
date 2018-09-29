package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 14:48
 * @description:
 **/
@Data
public class CompanyRemitDetailChartVO
{
    //客户编号
    private String orderId;
    //客户姓名
    private String customer_name;

    //身份证号
    private String customer_id_card;


    //业务员
    private String salesman_name;

    //大区
    private String biz_area;

    //省份---上牌地
    private String apply_license_plate_area;

    //业务团队
    private String partner_name;


    //贷款银行
    private String bank;


    //执行利率
    private String sign_rate;

    //车型
    private String car_type;



    //首付款
    private String down_payment_money;


    //银行分期本金
    private BigDecimal bank_period_principal;

    //打款金额
    private BigDecimal remit_amount;

    //提交时间
    private Date submitTime;

}
