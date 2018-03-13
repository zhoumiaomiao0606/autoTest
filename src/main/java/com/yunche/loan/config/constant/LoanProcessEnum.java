package com.yunche.loan.config.constant;

/**
 * Created by zhouguoliang on 2018/1/31.
 */
public enum LoanProcessEnum {

    START("start_process", "流程启动"),
    END("end_process", "流程终止"),
    CREDIT_APPLY("usertask_credit_apply", "发起征信申请"),
    CREDIT_APPLY_VERIFY("usertask_credit_apply_verify", "征信申请审核"),
    BANK_CREDIT_RECORD("usertask_bank_credit_record", "银行征信录入"),
    SOCIAL_CREDIT_RECORD("usertask_social_credit_record", "社会征信录入"),
    LOAN_APPROVE("usertask_loan_apply", "业务申请"),
    VISIT_VERIFY("usertask_visit_verify", "上门调查"),
    TELEPHONE_VERIFY("usertask_telephone_verify", "电审信息"),
    INFO_SUPPLEMENT("usertask_info_supplement", "资料增补"),

    //    TELEPHONE_VERIFY_REVIEW("usertask_telephone_verify_review", "电审复审信息"),
    CAR_INSURANCE_RECORD("usertask_car_insurance_record", "车辆保险"),
    GPS_INSTALL("usertask_gps_install", "GPS安装"),
    BUSINESS_EXAMINE("usertask_busi_examine", "业务审批"),
    CONTRACT_PRINT("usertask_contract_print", "合同套打"),
    LICENSE_MORTGAGE("usertask_license_mortgage", "上牌抵押"),
    PAY_APPROVE("usertask_pay_approve", "放款审批"),
    PAY_DONE("usertask_pay_process", "财务打款");

    private String code;

    private String name;

    LoanProcessEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
