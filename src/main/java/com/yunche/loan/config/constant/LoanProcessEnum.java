package com.yunche.loan.config.constant;

import org.apache.commons.lang3.EnumUtils;

/**
 * Created by zhouguoliang on 2018/1/31.
 */
public enum LoanProcessEnum {

    START("start_process", "流程启动"),
    END("end_process", "流程终止"),

    CREDIT_APPLY("usertask_credit_apply", "征信申请"),
    BANK_CREDIT_RECORD("usertask_bank_credit_record", "银行征信录入"),
    SOCIAL_CREDIT_RECORD("usertask_social_credit_record", "社会征信录入"),

    BANK_SOCIAL_CREDIT_RECORD_FILTER("filter_bank_social_credit_record", "征信记录拦截任务"),
    LOAN_APPLY_VISIT_VERIFY_FILTER("filter_loan_apply_visit_verify", "业务申请&上门调查拦截任务"),
//    BUSINESS_MATERIAL_REVIEW_FILTER("filter_business_material_review", "业务审批&资料审批拦截任务"),

    LOAN_APPLY("usertask_loan_apply", "业务申请"),
    VISIT_VERIFY("usertask_visit_verify", "上门调查"),

    TELEPHONE_VERIFY("usertask_telephone_verify", "电审信息"),

    INFO_SUPPLEMENT("usertask_info_supplement", "资料增补"),
    CREDIT_SUPPLEMENT("usertask_credit_supplement", "征信增补"),

    FINANCIAL_SCHEME("servicetask_financial_scheme", "金融方案展示"),

    VEHICLE_INFORMATION("usertask_vehicle_information", "提车资料"),
    APPLY_LICENSE_PLATE_DEPOSIT_INFO("usertask_apply_license_plate_deposit_info", "车辆抵押"),

    CAR_INSURANCE("usertask_car_insurance", "车辆保险"),
    INSTALL_GPS("usertask_install_gps", "GPS安装"),
    COMMIT_KEY("usertask_commit_key", "待收钥匙"),

    BUSINESS_REVIEW("usertask_business_review", "业务审批"),
    LOAN_REVIEW("usertask_loan_review", "放款审批"),
    REMIT_REVIEW("usertask_remit_review", "打款确认"),
    BANK_LEND_RECORD("usertask_bank_lend_record", "银行放款记录"),
    BANK_CARD_RECORD("usertask_bank_card_record", "银行卡录入"),

    MATERIAL_REVIEW("usertask_material_review", "资料审核"),
    MATERIAL_PRINT_REVIEW("usertask_material_print_review", "合同套打");

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

    public static String getNameByCode(String code) {

        for (LoanProcessEnum e : LoanProcessEnum.values()) {
            if (e.getCode().equals(code)) {

                return e.name;
            }
        }
        return null;
    }

    public static boolean havingCode(String code) {

        for (LoanProcessEnum e : LoanProcessEnum.values()) {
            if (e.getCode().equals(code)) {

                return true;
            }
        }
        return false;
    }
}
