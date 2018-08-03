/**
 * @author: ZhongMingxiao
 * @create: 2018-08-03 00:18
 * @description: 导出excel财务垫款明细
 **/
package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExportRemitDetailQueryVO
{
    //业务区域
    private String area_name;

    //客户姓名
    private String customer_name;

    //身份证号
    private String customer_id_card;

    //手机号
    private String customer_mobile;

    //贷款银行
    private String bank;

    //业务团队
    private String partner_name;

    //业务员
    private String salesman_name;

    //车型
    private String car_type;

    //车价
    private String price;

    //执行利率
    private String sign_rate;

    //首付款
    private String loan_amount;

    //贷款金额
    private String down_payment_money;

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
