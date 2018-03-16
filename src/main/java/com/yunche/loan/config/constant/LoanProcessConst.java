package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 消费贷流程常量
 *
 * @author liuzhe
 * @date 2018/2/27
 */
public class LoanProcessConst {
    /**
     * 打回
     */
    public static final Integer ACTION_REJECT = 0;
    /**
     * 通过
     */
    public static final Integer ACTION_PASS = 1;
    /**
     * 弃单
     */
    public static final Integer ACTION_CANCEL = 2;
    /**
     * 资料增补
     */
    public static final Integer ACTION_INFO_SUPPLEMENT = 3;

    /**
     * 任务状态:  0-全部;
     */
    public static final Integer TASK_ALL = 0;
    /**
     * 任务状态:  1-已处理(审核);
     */
    public static final Integer TASK_DONE = 1;
    /**
     * 任务状态:  2-未处理(待审核);
     */
    public static final Integer TASK_TODO = 2;
    /**
     * 任务状态：还未执行到当前任务节点
     */
    public static final Integer TASK_NOT_REACH_CURRENT = 3;

    /**
     * 删除理由  【task被删除的理由-任务已被执行】
     */
    public static final String DELETE_RELEASE_HAS_DONE = "completed";

    /**
     * 任务KEY-NAME映射
     */
    public static final Map<String, String> PROCESS_MAP = Maps.newHashMap();

    static {
        PROCESS_MAP.put("start_process", "流程启动");
        PROCESS_MAP.put("end_process", "流程终止");
        PROCESS_MAP.put("usertask_credit_apply", "发起征信申请");
        PROCESS_MAP.put("usertask_credit_apply_verify", "征信申请审核");
        PROCESS_MAP.put("usertask_bank_credit_record", "银行征信录入");
        PROCESS_MAP.put("usertask_social_credit_record", "社会征信录入");
        PROCESS_MAP.put("usertask_loan_apply", "业务申请");
        PROCESS_MAP.put("usertask_visit_verify", "上门调查");
        PROCESS_MAP.put("usertask_telephone_verify", "电审信息");
        PROCESS_MAP.put("usertask_info_supplement", "资料增补");

        //    TELEPHONE_VERIFY_REVIEW("usertask_telephone_verify_review", "电审复审信息");
        PROCESS_MAP.put("usertask_car_insurance_record", "车辆保险");
        PROCESS_MAP.put("usertask_gps_install", "GPS安装");
        PROCESS_MAP.put("usertask_busi_examine", "业务审批");
        PROCESS_MAP.put("usertask_contract_print", "合同套打");
        PROCESS_MAP.put("usertask_license_mortgage", "上牌抵押");
        PROCESS_MAP.put("usertask_pay_approve", "放款审批");
        PROCESS_MAP.put("usertask_pay_process", "财务打款");
    }
}
