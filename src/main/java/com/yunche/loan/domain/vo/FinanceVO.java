package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class FinanceVO {

    private String order_id;//业务编号
    private String customer_id;//主贷人编号
    private String cname;//主贷人姓名
    private String id_card;//主贷人身份证
    private String ename;//业务员
    private String mobile;//手机号
    private String loan_amount;//贷款金额
    private String pname;//合伙人
    private String pay_month;// 是否月结：0-否;1-是;
    private String return_rate_amount;// 返利
    private String beneficiary_bank;// 收款银行
    private String beneficiary_account;//收款账户
    private String beneficiary_account_number;//收款账号
    private String remit_amount;//打款金额
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
    private String install_gps_fee_type;//安装gps费 1 打款内扣 2 返利内扣 3 实收
    private String risk_fee_type;//风险费 1 打款内扣 2 返利内扣 3 实收
    private String fair_assess_fee_type;//公正评估费 1 打款内扣 2 返利内扣 3 实收
    private String apply_license_plate_out_province_fee_type;//上省外牌费 1 打款内扣 2 返利内扣 3 实收
    private String based_margin_fee_type;//基础保证金 1 打款内扣 2 返利内扣 3 实收
    private String total_cost;//费用总额
    private String complete_material_date;//资料完整日期
    private String rate_type;//手续费收取方式 1 一次性 2 分期
    private String is_pledge;//是否抵押 0-否;1-是;
    private String is_guarantee;//是否担保 0-否;1-是;
    private String biz_area;//业务区域
    private String financial_product_name;//金融产品名称
    private String loan_time;//贷款时间
    private String bank;//贷款银行
    private String bank_period_principal;//银行分期本金
    private String bank_per_rate;//银行分期比例
    private String bank_fee;//贷款利息/银行手续费
    private String down_payment_money;//首付款
    private String down_payment_ratio;//首府比例
    private String sign_rate;//执行利率
    private String first_month_repay;//银行首月还款
    private String each_month_repay;//月还款
    private String gps_num;//gps安装个数
    private String car_key;//是否留备用钥匙 0-否;1-是;
    private String car_name;//车名
    private String sale_price;//车价格
    private String assess_price;//评估价格
    private String production_type;//生产类型（1;//国产;2;//合资）
    private String license_plate_type;//牌证类型 1 公牌 2 私牌
    private String car_type;//车辆类型：1-新车; 2-二手车; 3-不限;
    private String apply_license_plate_area_id;//上牌地
    private String apply_license_plate_parent_area_id;//上牌地
    private String visit_date;//调查时间
    private String vname;//调查人
    private String visit_address;//调查地址
    private String survey_report;//调查报告
    private String verify_status;//电审结果
    private String verify_report;//电审描述

}
