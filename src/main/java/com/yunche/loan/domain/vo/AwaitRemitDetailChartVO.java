package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 11:14
 * @description:
 **/
@Data
public class AwaitRemitDetailChartVO
{

    //客户姓名
    private String customer_name;

    //身份证号
    private String customer_id_card;


    //业务团队
    private String partner_name;

    //业务员
    private String salesman_name;

    //经办人
    private String operationUser;
    //经办时间
    private Date operationDate;
    //执行利率
    private String sign_rate;


    //贷款金额
    private String loan_amount;

    //银行分期本金
    private String bank_period_principal;

    //打款金额
    private String remit_amount;

}
