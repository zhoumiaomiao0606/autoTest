package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancialSchemeVO {

    //车辆价格
    private String car_price;

    //业务产品
    private String financial_product_name;

    //贷款银行
    private String bank;

    //签约利率
    private String sign_rate;

    //首付额
    private String down_payment_money;

    //贷款额
    private String loan_amount;

    //首府比例
    private String down_payment_ratio;

    //按揭期限
    private String loan_time;

    //银行分期本金
    private String bank_period_principal;

    //银行手续费
    private String bank_fee;

    //实际成交价格
    private String actual_car_price;

    //首月还款
    private String first_month_repay;

    //本月还款
    private String each_month_repay;


    private String order_id;//订单id
    private String customer_id;//客户id
    private String cname;//主贷人姓名
    private String id_card;//身份证

    private String financial_product_id;//产品ID
    private String license_plate_number;//车牌号
    private String registration_certificate_number;//登记证号
    private String apply_license_plate_date;//上牌日期
    private String apply_license_plate_deposit_date;//抵押办理日期










    private String principal_interest_sum;//本息合记

    private String financial_bank_staging_ratio;
    private String bank_base_rate;//银行基准利率

    private BigDecimal applayQuota;//申请本币额度

    private String category_superior;//产品大类
    private String appraisal;//基准评估价

    private Byte customer_signature_type;//签单类型

    private String customer_ctelzone;//客户单位区号
    private String partner_area_id;//合伙人区域ID
    private String partner_area_name;//合伙人区域名称
    private String partner_name;//合伙人


}
