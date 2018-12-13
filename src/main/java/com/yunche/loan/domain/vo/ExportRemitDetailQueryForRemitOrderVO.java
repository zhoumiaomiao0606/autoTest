package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-28 18:27
 * @description:
 **/
@Data
public class ExportRemitDetailQueryForRemitOrderVO
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
    private String down_payment_money;

    //贷款金额
    private String loan_amount;

    //银行分期本金
    private String bank_period_principal;

    //打款金额
    private String remit_amount;


      //公司收益
      private String service_fee;
      //履约金
      private String performance_fee;
      //上牌押金
      private String apply_license_plate_deposit_fee;
      //GPS使用费
      private String install_gps_fee;
      //风险金
      private String risk_fee;
      //公正评估费
      private String fair_assess_fee;
      //上省外牌
      private String apply_license_plate_out_province_fee;
      //基础保证金
      private String based_margin_fee;
      //其他
      private String other_fee;
      //返利不内扣
      private String rebate_not_deducted;
      //返利金额
      private String remit_return_rate_amount;
      //额外费用
      private String extra_fee;

    //创建时间
    private String gmt_create;

    //业务审批时间
    private Date businessReviewTime;

    //业务审批人
    private String businessReviewUsername;

    //放款审批时间
    private Date loanReviewTime;

    //放款审批人
    private String loanReviewUsername;

    //提交时间--垫款时间
    private String submitTime;

    //退款时间
    private String refundTime;

    //提交人
    private String username;
}
