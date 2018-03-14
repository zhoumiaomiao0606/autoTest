package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class BusinessReviewVO {
    private String order_id;//业务编号
    private String customer_id;//客户编号
    private String cname;//主贷人姓名
    private String ename;//业务员
    private String id_card;//证件号码
    private String pname;//合伙人
    private String loan_amount;//贷款金额
    private String sign_rate;//执行利率
    private String bank_period_principal;//银行分期本金
    private String bank_fee;//银行手续费
    private String pay_month;//是否月结  0 否 1 是
    private String service_fee;//服务费
    private String apply_license_plate_deposit_fee;//上牌押金
    private String performance_fee;//履约金
    private String install_gps_fee;//安装gps费用
    private String risk_fee;//风险费用
    private String fair_assess_fee;//公正评估费
    private String apply_license_plate_out_province_fee;//上省外牌费用
    private String based_margin_fee;//基础保证金
    private String service_fee_type;//服务费  1 打款内扣 2 返利内扣 3 实收
    private String apply_license_plate_deposit_fee_type;//上牌押金 1 打款内扣 2 返利内扣 3 实收
    private String performance_fee_type;//履约金 1 打款内扣 2 返利内扣 3 实收
    private String install_gps_fee_type;//安装gps费用 1 打款内扣 2 返利内扣 3 实收
    private String risk_fee_type;//风险费用 1 打款内扣 2 返利内扣 3 实收
    private String fair_assess_fee_type;//公正评估费用 1 打款内扣 2 返利内扣 3 实收
    private String apply_license_plate_out_province_fee_type;//上省外牌费用 1 打款内扣 2 返利内扣 3 实收
    private String based_margin_fee_type;//基础保证金 1 打款内扣 2 返利内扣 3 实收
    private String total_cost;//费用总额
    private String beneficiary_bank;//收款银行
    private String beneficiary_account;//收款账户
    private String beneficiary_account_number;//收款账号
    private String return_rete_amount;//返利金额
    private String remit_amount;//打款金额

}
