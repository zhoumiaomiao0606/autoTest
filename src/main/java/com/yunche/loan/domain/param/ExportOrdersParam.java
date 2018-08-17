package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class ExportOrdersParam {
    private String order_id;//业务编号
    private String customer_name;//客户编号
    private String customer_id_card;//身份证
    private String customer_mobile;//手机号
    private String saleman;//业务员
    private String partner_name;//合伙人
    private String car_price_start;//车价格
    private String car_price_end;//车价格
    private String plan_bank;//贷款银行
    private String plan_appraisal_start;//基准评估价
    private String plan_appraisal_end;//基准评估价
    private String plan_loan_amount_start;//贷款金额
    private String plan_loan_amount_end;//贷款金额
    private String plan_bank_period_principal_start;//银行分期本金
    private String plan_bank_period_principal_end;//银行分期本金
    private String remit_amount_start;//打款金额
    private String remit_amount_end;//打款金额
    private String plan_loan_time_start;//期数
    private String plan_loan_time_end;//期数
    private String car_name;//车型
    private String car_type;//车类型
    private String vehicle_license_plate_number;//车牌号
    private String vehicle_engine_number;//车辆引擎
    private String vehicle_identification_number;//车架号
    private String vehicle_register_date_start;//车辆注册日期
    private String vehicle_register_date_end;//车辆注册日期
    private String vehicle_color;//车颜色
    private String vehicle_displacement;//车排量
    private String vehicle_use_year_start;//使用年限
    private String vehicle_use_year_end;//使用年限
    private String usertask_credit_apply_create_time_start;//征信申请时间
    private String usertask_credit_apply_create_time_end;//征信申请时间
    private String usertask_bank_credit_record_create_time_start;//银行征信时间
    private String usertask_bank_credit_record_create_time_end;//银行征信时间
    private String usertask_social_credit_record_create_time_start;//社会征信时间
    private String usertask_social_credit_record_create_time_end;//社会征信时间
    private String usertask_loan_apply_create_time_start;//贷款申请时间
    private String usertask_loan_apply_create_time_end;//贷款申请时间
    private String usertask_visit_verify_create_time_start;//上门家纺时间
    private String usertask_visit_verify_create_time_end;//上门家纺时间
    private String usertask_telephone_verify_create_time_start;//电审时间
    private String usertask_telephone_verify_create_time_end;//电审时间
    private String usertask_vehicle_information_create_time_start;//提车录入时间
    private String usertask_vehicle_information_create_time_end;//提车录入时间
    private String usertask_material_review_create_time_start;//资料审核时间
    private String usertask_material_review_create_time_end;//资料审核时间
    private String usertask_material_print_review_create_time_start;//合同套打时间
    private String usertask_material_print_review_create_time_end;//合同套打时间
    private String usertask_install_gps_create_time_start;//gps安装时间
    private String usertask_install_gps_create_time_end;//gps安装时间
    private String usertask_apply_license_plate_deposit_info_create_time_start;//抵押时间
    private String usertask_apply_license_plate_deposit_info_create_time_end;//抵押时间
    private String usertask_car_insurance_create_time_start;//车辆保险时间
    private String usertask_car_insurance_create_time_end;//车辆保险时间
    private String usertask_business_pay_create_time_start;//业务付款时间
    private String usertask_business_pay_create_time_end;//业务付款时间
    private String usertask_business_review_create_time_start;//业务审核时间
    private String usertask_business_review_create_time_end;//业务审核时间
    private String usertask_loan_review_create_time_start;//业务审核时间
    private String usertask_loan_review_create_time_end;//业务审核时间
    private String usertask_remit_review_create_time_start;//打款审核时间
    private String usertask_remit_review_create_time_end;//打款审核时间
    private String usertask_material_manage_create_time_start;//合同归档时间
    private String usertask_material_manage_create_time_end;//合同归档时间
    private String usertask_bank_lend_record_create_time_start;//银行放款时间
    private String usertask_bank_lend_record_create_time_end;//银行放款时间
    private String usertask_bank_card_send_create_time_start;//银行寄送时间
    private String usertask_bank_card_send_create_time_end;//银行寄送时间

    List<Long> partnerList;

    List<String> bankList;

}
