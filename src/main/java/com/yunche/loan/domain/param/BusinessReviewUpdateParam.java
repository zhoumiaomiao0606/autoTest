package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Data
public class BusinessReviewUpdateParam  {

    //规则信息集合
    private List<RuleDetailPara> listRules;

    private String listrule;

    private String  rebateFirst;   //第一道返利

    private String rebateSecond;   //第二道返利

    @NotBlank
    private String order_id;//订单号
    @NotBlank
    private String service_fee;//服务费--公司收益
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
    private String extra_fee;//额外费用
    @NotBlank
    private String extra_fee_type;
    @NotBlank
    private String other_fee;//其他费用
    @NotBlank
    private String other_fee_type;
    @NotBlank
    private String beneficiary_bank;//收款银行
    @NotBlank
    private String beneficiary_account;//收款账户
    @NotBlank
    private String beneficiary_account_number;//收款账号

    private String bank_code;

    private String child_bank;

    private String child_bank_code;
    @NotBlank
    private String return_rate_amount;//返利金额
    @NotBlank
    private String remit_amount;//打款金额
    @NotBlank
    private String insurance_situation;//保险情况
    @NotBlank
    private String rebate_not_deducted;//返利不内扣

    private String partner_rebate_amount;//合伙人返利金额

    private String key_risk_premium_type;
}
