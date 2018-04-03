package com.yunche.loan.config.constant;

/**
 * 贷款单审核操作 -流程变量
 *
 * @author liuzhe
 * @date 2018/3/15
 */
public class LoanProcessVariableConst {
    /**
     * 审核结果
     */
    public static final String PROCESS_VARIABLE_ACTION = "action";

    /**
     * 打回来源
     */
    public static final String PROCESS_VARIABLE_REJECT_ORIGIN_TASK = "reject_origin_task";

    /**
     * 审核备注
     */
    public static final String PROCESS_VARIABLE_INFO = "info";
    /**
     * 打回目标节点
     */
    public static final String PROCESS_VARIABLE_TARGET = "target";
    /**
     * 电审进度：审核角色进程
     */
    public static final String PROCESS_VARIABLE_TELEPHONE_VERIFY_ROLE_LEVEL_PROGRESS = "telephone_verify_role_level_process";
    /**
     * 审核人ID
     */
    public static final String PROCESS_VARIABLE_USER_ID = "user_id";
    /**
     * 审核人姓名
     */
    public static final String PROCESS_VARIABLE_USER_NAME = "user_name";
    /**
     * 操作员所属角色 OR 所属合伙人团队名称
     */
    public static final String PROCESS_VARIABLE_USER_GROUP = "user_group";
    /**
     * 贷款额
     */
    public static final String PROCESS_VARIABLE_LOAN_AMOUNT = "loanAmount";
    /**
     * 资料增补类型
     */
    public static final String PROCESS_VARIABLE_INFO_SUPPLEMENT_TYPE = "info_supplement_type";
    /**
     * 资料增补内容
     */
    public static final String PROCESS_VARIABLE_INFO_SUPPLEMENT_CONTENT = "info_supplement_content";
    /**
     * 资料增补说明
     */
    public static final String PROCESS_VARIABLE_INFO_SUPPLEMENT_INFO = "info_supplement_info";
    /**
     * 增补源头任务节点：从哪个节点发起的增补
     */
    public static final String PROCESS_VARIABLE_INFO_SUPPLEMENT_ORIGIN_TASK = "info_supplement_origin_task";

}
