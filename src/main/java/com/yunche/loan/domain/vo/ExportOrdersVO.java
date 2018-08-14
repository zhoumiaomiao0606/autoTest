package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExportOrdersVO {

    private String order_id;//业务编号
    private String customer_name;//客户姓名
    private String customer_id_card;//身份证
    private String customer_mobile;//手机号
    private String saleman;//业务员
    private String partner_name;//合伙人
    private String car_price;//车价
    private String plan_bank;//贷款银行
    private String plan_appraisal;//基准品估计
    private String plan_loan_amount;//贷款金额
    private String plan_bank_period_principal;//银行分期本金
    private String remit_amount;//打款金额
    private String plan_loan_time;//期数
    private String car_name;//车型
    private String car_type;//车辆类型
    private String vehicle_license_plate_number;//车牌号
    private String vehicle_engine_number;//车辆引擎编号
    private String vehicle_identification_number;//车架号
    private String vehicle_register_date;//车辆注册日期
    private String vehicle_color;//车辆颜色
    private String vehicle_displacement;//车排量
    private String vehicle_use_year;//车辆使用年限
    private String usertask_credit_apply_create_time;//征信申请时间
    private String usertask_bank_credit_record_create_time;//银行征信申请时间
    private String usertask_social_credit_record_create_time;//社会征信申请时间
    private String usertask_loan_apply_create_time;//贷款申请时间
    private String usertask_visit_verify_create_time;//上门家访时间
    private String usertask_telephone_verify_create_time;//电审时间
    private String usertask_vehicle_information_create_time;//提车资料录入时间
    private String usertask_material_review_create_time;//资料审核时间
    private String usertask_material_print_review_create_time;//合同套打时间
    private String usertask_install_gps_create_time;//gps安装时间
    private String usertask_apply_license_plate_deposit_info_create_time;//上牌抵押时间
    private String usertask_car_insurance_create_time;//车辆保险录入时间
    private String usertask_business_pay_create_time;//业务付款时间
    private String usertask_business_review_create_time;//业务审批时间
    private String usertask_loan_review_create_time;//放款审核时间
    private String usertask_remit_review_create_time;//打款审核时间
    private String usertask_material_manage_create_time;//资料归档时间
    private String usertask_bank_lend_record_create_time;//银行放款时间
    private String usertask_bank_card_send_create_time;//银行卡寄送时间
}
