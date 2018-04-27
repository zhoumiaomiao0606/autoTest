package com.yunche.loan.config.constant;

import lombok.Data;

public enum  ProcessKeyOrderByEnum {


        START("start_process",1),
        END("end_process",2),

        CREDIT_APPLY("usertask_credit_apply", 3),
        BANK_CREDIT_RECORD("usertask_bank_credit_record", 4),
        SOCIAL_CREDIT_RECORD("usertask_social_credit_record", 5),

        BANK_SOCIAL_CREDIT_RECORD_FILTER("filter_bank_social_credit_record", 6),
        LOAN_APPLY_VISIT_VERIFY_FILTER("filter_loan_apply_visit_verify", 7),

        LOAN_APPLY("usertask_loan_apply", 8),
        VISIT_VERIFY("usertask_visit_verify", 9),

        TELEPHONE_VERIFY("usertask_telephone_verify",10),

        INFO_SUPPLEMENT("usertask_info_supplement",11),
        CREDIT_SUPPLEMENT("usertask_credit_supplement",12),
        FINANCIAL_SCHEME_MODIFY_APPLY("usertask_financial_scheme_modify_apply",13),
        REFUND_APPLY("usertask_refund_apply",14),
        FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW("usertask_financial_scheme_modify_apply_review",15),
        REFUND_APPLY_REVIEW("usertask_refund_apply_review",16),

        FINANCIAL_SCHEME("servicetask_financial_scheme",17),

        VEHICLE_INFORMATION("usertask_vehicle_information",18),
        APPLY_LICENSE_PLATE_DEPOSIT_INFO("usertask_apply_license_plate_deposit_info",19),

        BANK_CARD_RECORD("usertask_bank_card_record",20),
        CAR_INSURANCE("usertask_car_insurance",21),
        INSTALL_GPS("usertask_install_gps",22),
        COMMIT_KEY("usertask_commit_key",23),

        MATERIAL_REVIEW("usertask_material_review",24),
        MATERIAL_PRINT_REVIEW("usertask_material_print_review",25),

        BUSINESS_REVIEW("usertask_business_review",26),
        LOAN_REVIEW("usertask_loan_review",27),
        REMIT_REVIEW("usertask_remit_review",28),
        BANK_LEND_RECORD("usertask_bank_lend_record",29);

        private String code;

        private Integer order;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }


        ProcessKeyOrderByEnum(String code, Integer order) {
            this.code = code;
            this.order = order;
        }


        public static Integer getOrderByCode(String code) {

            for (ProcessKeyOrderByEnum e : ProcessKeyOrderByEnum.values()) {
                if (e.getCode().equals(code)) {
                    return e.order;
                }
            }
            return 9999999;
        }
}
