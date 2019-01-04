package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalCostDetailsVO
{

    private String key_risk_premium_fee;

    private String key_risk_premium_type;

    //服务费
    private String cost_service_fee;
    //上牌押金
    private String cost_apply_license_plate_deposit_fee;
    //履约金
    private String cost_performance_fee;
    //安装gps费用
    private String cost_install_gps_fee;
    //风险费用
    private String cost_risk_fee;
    //公正评估费
    private String cost_fair_assess_fee;
    //上省外拍照费
    private String cost_apply_license_plate_out_province_fee;
    //基础保证金
    private String cost_based_margin_fee;
    //额外费用
    private String cost_extra_fee;
    //其他费用
    private String cost_other_fee;
    //返利不内扣
    private String rebate_not_deducted;

    //1.打款内扣  2.返利内扣  3.实收
    private String cost_service_fee_type;
    private String cost_apply_license_plate_deposit_fee_type;
    private String cost_performance_fee_type;
    private String cost_install_gps_fee_type;
    private String cost_risk_fee_type;
    private String cost_fair_assess_fee_type;
    private String cost_apply_license_plate_out_province_fee_type;
    private String cost_based_margin_fee_type;
    private String cost_extra_fee_type;
    private String cost_other_fee_type;
    private String remit_amount;
    private String remit_return_rate_amount;
    private String partner_rebate_amount;


}
