package com.yunche.loan.domain.param;

import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
public class BusinessReviewCalculateParam {
    @NotBlank
    private String service_fee;//服务费
    @NotBlank
    private String apply_license_plate_deposit_fee;//上牌押金
    @NotBlank
    private String performance_fee;//履约金
    @NotBlank
    private String install_gps_fee;//安装gps费用
    @NotBlank
    private String risk_fee;//风险费用
    @NotBlank
    private String fair_assess_fee;//公正评估费
    @NotBlank
    private String apply_license_plate_out_province_fee;//上省外牌费用
    @NotBlank
    private String based_margin_fee;//基础保证金
    @NotBlank
    private String service_fee_type;//服务费  1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String apply_license_plate_deposit_fee_type;//上牌押金 1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String performance_fee_type;//履约金 1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String install_gps_fee_type;//安装gps费用 1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String risk_fee_type;//风险费用 1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String fair_assess_fee_type;//公正评估费用 1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String apply_license_plate_out_province_fee_type;//上省外牌费用 1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String based_margin_fee_type;//基础保证金 1 打款内扣 2 返利内扣 3 实收
    @NotBlank
    private String extra_fee;
    @NotBlank
    private String extra_fee_type;
    @NotBlank
    private String other_fee;
    @NotBlank
    private String other_fee_type;
    @NotBlank
    private String return_rate_amount;//返利金额
    @NotBlank
    private String bank_period_principal;//银行分期本金
    @NotBlank
    private String pay_month;// 0 否 1 是 是否月结
}
