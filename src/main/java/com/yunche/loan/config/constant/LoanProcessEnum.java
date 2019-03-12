package com.yunche.loan.config.constant;

/**
 * Created by zhouguoliang on 2018/1/31.
 */
public enum LoanProcessEnum {

    START("start_process", "流程启动"),
    END("end_process", "流程终止"),


/////////////////////////////////////////////////// ↓↓↓↓↓--消费贷流程--↓↓↓↓↓ /////////////////////////////////////////////////////////

    // 电审前
    CREDIT_APPLY("usertask_credit_apply", "征信申请"),

    BANK_CREDIT_RECORD("usertask_bank_credit_record", "银行征信录入"),
    BANK_CREDIT_RECORD_B("usertask_bank_credit_record_B", "银行征信录入B"),
    SOCIAL_CREDIT_RECORD("usertask_social_credit_record", "社会征信录入"),
    LOAN_INFO_RECORD("usertask_loan_info_record", "视频面签登记"),

    LOAN_APPLY("usertask_loan_apply", "贷款申请"),
    VISIT_VERIFY("usertask_visit_verify", "上门调查"),

    BANK_OPEN_CARD("usertask_bank_open_card", "银行开卡"),
    TELEPHONE_VERIFY("usertask_telephone_verify", "电审"),


    // 电审后
    FINANCIAL_SCHEME("servicetask_financial_scheme", "金融方案"),

    VEHICLE_INFORMATION("usertask_vehicle_information", "提车资料"),
    APPLY_LICENSE_PLATE_DEPOSIT_INFO("usertask_apply_license_plate_deposit_info", "车辆抵押"),

    CAR_INSURANCE("usertask_car_insurance", "车辆保险"),
    INSTALL_GPS("usertask_install_gps", "GPS安装"),
    COMMIT_KEY("usertask_commit_key", "待收钥匙"),
    VIDEO_REVIEW("usertask_video_review", "视频审核"),
    UNDER_LINE_VIDEO_REVIEW("usertask_under_line_video_review", "线下视频审核"),

    MATERIAL_REVIEW("usertask_material_review", "资料审核"),
    MATERIAL_PRINT_REVIEW("usertask_material_print_review", "合同套打"),
    MATERIAL_MANAGE("usertask_material_manage", "合同归档"),
    APPLY_INSTALMENT("usertask_apply_instalment", "申请分期"),

    MATERIAL_PRINT_REVIEW1("usertask_material_print_review1", "合同套打"),
    APPLY_INSTALMENT1("usertask_apply_instalment1", "申请分期"),
    APPLY_INSTALMENT_REVIEW("usertask_apply_instalment_review", "申请分期审核"),

    BUSINESS_PAY("usertask_business_pay", "业务付款申请"),
    BUSINESS_REVIEW("usertask_business_review", "业务审批"),
    LOAN_REVIEW("usertask_loan_review", "放款审批"),
    REMIT_REVIEW("usertask_remit_review", "打款确认"),

    BANK_LEND_RECORD("usertask_bank_lend_record", "银行放款记录"),
    BANK_CARD_RECORD("usertask_bank_card_record", "银行卡接收"),
    BANK_CARD_SEND("usertask_bank_card_send", "银行卡寄送"),
    CUSTOMER_REPAY_PLAN("usertask_customer_repay_plan", "客户还款计划"),


    ///////////////////////////////////////////// ↓↓↓↓↓--filter--↓↓↓↓↓ /////////////////////////////////////////////////
    BANK_SOCIAL_CREDIT_RECORD_FILTER("filter_bank_social_credit_record", "征信记录-拦截任务"),
    LOAN_APPLY_VISIT_VERIFY_FILTER("filter_loan_apply_visit_verify", "业务申请&上门调查-拦截任务"),
    REMIT_REVIEW_FILTER("filter_remit_review", "打款确认-拦截任务"),
    DATA_FLOW_MORTGAGE_P2C_NEW_FILTER("filter_data_flow_mortgage_p2c_new", "005-抵押资料合伙人至公司-新建-拦截任务"),
    APPLY_INSTEAD_PAY_FILTER("filter_apply_instead_pay", "申请代偿-开始-拦截任务"),
    VIDEO_REVIEW_BEFORE_FILTER("filter_video_review_before", "视频审核-拦截任务(先)"),
    VIDEO_REVIEW_FILTER("filter_video_review", "视频审核-拦截任务"),
    UNDER_LINE_VIDEO_REVIEW_FILTER("filter_under_line_video_review", "线下视频审核-拦截任务"),
    UNDER_LINE_VIDEO_REVIEW_BEFORE_FILTER("filter_under_line_video_review_before", "线下视频审核-拦截任务(先)"),
    ///////////////////////////////////////////// ↑↑↑↑↑--filter--↑↑↑↑↑ /////////////////////////////////////////////////

    /////////////////////////////////////////// ↓↓↓↓↓--流程外的节点--↓↓↓↓↓ //////////////////////////////////////////////
    BANK_OPEN_CARD_LIST("usertask_bank_open_card_list", "银行开卡清单"),

    INFO_SUPPLEMENT("usertask_info_supplement", "资料增补"),
    CREDIT_SUPPLEMENT("usertask_credit_supplement", "征信增补"),

    FINANCIAL_SCHEME_MODIFY_APPLY("usertask_financial_scheme_modify_apply", "金融方案修改"),
    FINANCIAL_SCHEME_MODIFY_APPLY_REVIEW("usertask_financial_scheme_modify_apply_review", "金融方案审核"),
    REFUND_APPLY("usertask_refund_apply", "退款申请"),
    REFUND_APPLY_REVIEW("usertask_refund_apply_review", "退款申请审核"),

    OUTWORKER_COST_APPLY("usertask_outworker_cost_apply", "外勤费用申报"),
    OUTWORKER_COST_APPLY_REVIEW("usertask_outworker_cost_apply_review", "财务报销"),

    CUSTOMER_REPAY_PLAN_RECORD("usertask_customer_repay_plan_record", "银行还款计划记录"),
    INSURANCE_SCENE_RECORD("usertask_insurance_scene_record", "出险登记"),
    URGE_INSURANCE_WORK_BENCH("usertask_urge_insurance_work_bench", "催保工作台"),
    URGE_INSURANCE_ASSIGN_WORK_BENCH("usertask_urge_insurance_assign_work_bench", "催保分配工作台"),

    COLLECTION_WORKBENCH("usertask_collection_workbench", "电催处理"),
    OVERDUE_COLLECTION_LIST("usertask_overdue_collection_list", "逾期催缴清单文件"),
    UN_MORTGAGE_FILE("usertask_un_mortgage_file", "未抵押明细文件"),

    ROLE_CHANGE("usertask_role_change", "角色变更"),

    USERTASK_CONTRACT_OVERDUE("usertask_contract_overdue", "合同超期"),
    /////////////////////////////////////////// ↑↑↑↑↑--流程外的节点--↑↑↑↑↑ //////////////////////////////////////////////


    ////////////////////////////////////////// ↓↓↓↓↓--资料流转--↓↓↓↓↓ ///////////////////////////////////////////////////
    DATA_FLOW("usertask_data_flow", "资料流转汇总KEY"),

    // 合同资料
    DATA_FLOW_CONTRACT_P2C("usertask_data_flow_contract_p2c", "001-合同资料合伙人至公司"),
    DATA_FLOW_CONTRACT_P2C_REVIEW("usertask_data_flow_contract_p2c_review", "002-合同资料合伙人至公司-确认接收"),

    DATA_FLOW_CONTRACT_C2B("usertask_data_flow_contract_c2b", "003-合同资料公司至银行"),
    DATA_FLOW_CONTRACT_C2B_REVIEW("usertask_data_flow_contract_c2b_review", "004-合同资料公司至银行-确认接收"),

    // 抵押资料
    DATA_FLOW_MORTGAGE_P2C("usertask_data_flow_mortgage_p2c", "005-抵押资料合伙人至公司"),
    DATA_FLOW_MORTGAGE_P2C_REVIEW("usertask_data_flow_mortgage_p2c_review", "006-抵押资料合伙人至公司-确认接收"),

    DATA_FLOW_MORTGAGE_C2B("usertask_data_flow_mortgage_c2b", "007-抵押资料公司至银行"),
    DATA_FLOW_MORTGAGE_C2B_REVIEW("usertask_data_flow_mortgage_c2b_review", "008-抵押资料公司至银行-确认接收"),

    DATA_FLOW_MORTGAGE_B2C("usertask_data_flow_mortgage_b2c", "009-抵押资料银行至公司"),
    DATA_FLOW_MORTGAGE_B2C_REVIEW("usertask_data_flow_mortgage_b2c_review", "010-抵押资料银行至公司-确认接收"),

    DATA_FLOW_MORTGAGE_C2P("usertask_data_flow_mortgage_c2p", "011-抵押资料公司至合伙人"),
    DATA_FLOW_MORTGAGE_C2P_REVIEW("usertask_data_flow_mortgage_c2p_review", "012-抵押资料公司至合伙人-确认接收"),

    // 权证资料
    DATA_FLOW_REGISTER_P2C("usertask_data_flow_register_p2c", "013-权证资料合伙人至公司"),
    DATA_FLOW_REGISTER_P2C_REVIEW("usertask_data_flow_register_p2c_review", "014-权证资料合伙人至公司-确认接收"),

    DATA_FLOW_REGISTER_C2B("usertask_data_flow_register_c2b", "015-权证资料公司至银行"),
    DATA_FLOW_REGISTER_C2B_REVIEW("usertask_data_flow_register_c2b_review", "016-权证资料公司至银行-确认接收"),
    ////////////////////////////////////////// ↑↑↑↑↑--资料流转--↑↑↑↑↑ ///////////////////////////////////////////////////


////////////////////////////////////////////////// ↑↑↑↑↑--消费贷流程--↑↑↑↑↑ //////////////////////////////////////////////////////////


    //////////////////////////////////////////// ↓↓↓↓↓--代偿--↓↓↓↓↓ /////////////////////////////////////////////////////
    APPLY_INSTEAD_PAY("usertask_apply_instead_pay", "申请代偿"),
    FINANCE_INSTEAD_PAY_REVIEW("usertask_finance_instead_pay_review", "财务代偿-确认"),
    PARTNER_INSTEAD_PAY("usertask_partner_instead_pay", "合伙人代偿"),
    PARTNER_INSTEAD_PAY_REVIEW("usertask_partner_instead_pay_review", "合伙人代偿-确认"),
    //////////////////////////////////////////// ↑↑↑↑↑--代偿--↑↑↑↑↑ /////////////////////////////////////////////////////


    //////////////////////////////////////////// ↓↓↓↓↓--催收--↓↓↓↓↓ /////////////////////////////////////////////////////
    VISIT_COLLECTION_REVIEW("usertask_visit_collection_review", "上门拖车-审核"),
    VISIT_COLLECTION("usertask_visit_collection", "上门拖车"),
    CAR_HANDLE("usertask_car_handle", "车辆处理"),
    CAR_OUT("usertask_car_out", "车辆出库"),
    SETTLE_ORDER("usertask_settle_order", "结清"),

    LEGAL_REVIEW("usertask_legal_review", "法务审核"),
    LEGAL_RECORD("usertask_legal_record", "法务登记"),
    //////////////////////////////////////////// ↑↑↑↑↑--催收--↑↑↑↑↑ /////////////////////////////////////////////////////


    ///////////////////////////////////////// ↓↓↓↓↓--三方过桥资金--↓↓↓↓↓ /////////////////////////////////////////////////
    BRIDGE_HANDLE("usertask_bridge_handle", "过桥处理(乙)"),
    BRIDGE_REPAY_RECORD("usertask_bridge_repay_record", "还款登记(甲)"),
    //    BRIDGE_INTEREST_RECORD("usertask_bridge_interest_record", "息费登记(甲)"),
    BRIDGE_REPAY_INFO("usertask_bridge_repay_info", "还款信息(甲乙)"),

    DATA_MANAGEMENT("usertask_data_management","资料管理");
    //////////////////////////////////////// ↑↑↑↑↑--三方过桥资金----↑↑↑↑↑ ////////////////////////////////////////////////


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