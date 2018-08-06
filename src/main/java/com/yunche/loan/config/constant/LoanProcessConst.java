package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

import static com.yunche.loan.config.constant.LoanProcessEnum.*;

/**
 * 消费贷流程常量
 *
 * @author liuzhe
 * @date 2018/2/27
 */
public class LoanProcessConst {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 打回:自动
     */
    public static final Byte ACTION_REJECT_AUTO = -1;
    /**
     * 打回:手动
     */
    public static final Byte ACTION_REJECT_MANUAL = 0;
    /**
     * 通过
     */
    public static final Byte ACTION_PASS = 1;
    /**
     * 弃单
     */
    public static final Byte ACTION_CANCEL = 2;
    /**
     * 资料增补
     */
    public static final Byte ACTION_INFO_SUPPLEMENT = 3;
    /**
     * 新建xx任务单
     */
    public static final Byte ACTION_NEW_TASK = 4;
    /**
     * 反审（回退）
     */
    public static final Byte ACTION_ROLL_BACK = 5;
    /**
     * 停止一条支线流程
     */
    public static final Byte ACTION_STOP_ONE_FLOW = 6;

    /**
     * 发起任务申请
     */
//    public static final Byte ACTION_LAUNCH_TASK_APPLY = 5;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 任务状态:  0-全部;
     */
    public static final Byte TASK_ALL = 0;
    /**
     * 任务状态:  1-已处理(审核);
     */
    public static final Byte TASK_DONE = 1;
    /**
     * 任务状态:  2-未处理(待审核);
     */
    public static final Byte TASK_TODO = 2;
    /**
     * 任务状态：还未执行到当前任务节点
     */
    public static final Byte TASK_NOT_REACH_CURRENT = 3;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 任务类别：1-未提交;
     */
    public static final Byte TASK_TYPE_UN_SUBMIT = 1;
    public static final String TASK_TYPE_TEXT_UN_SUBMIT = "打回";
    /**
     * 任务类别：2-(被)打回;
     */
    public static final Byte TASK_TYPE_REJECT = 2;
    public static final String TASK_TYPE_TEXT_REJECT = "未提交";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 业务申请：[打回] - 自于【资料审核】
     */
    public static final Byte LOAN_APPLY_REJECT_FROM_METERIAL_REVIEW = 1;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//    /**
//     * 删除理由  【task被删除的理由-任务已被执行】
//     */
//    public static final String DELETE_RELEASE_HAS_DONE = "completed";


    public static final String PARALLEL_TASK_FILTER_RELEASE_CANCEL = "filter_auto_cancel";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 任务KEY-NAME映射
     */
    public static final Map<String, String> TASK_USER_GROUP_MAP = Maps.newHashMap();

    static {
        TASK_USER_GROUP_MAP.put(START.getCode(), "流程启动");
        TASK_USER_GROUP_MAP.put(END.getCode(), "流程终止");
        TASK_USER_GROUP_MAP.put(CREDIT_APPLY.getCode(), "业务员");
//        TASK_USER_GROUP_MAP.put(CREDIT_APPLY_VERIFY.getCode(), "征信资料审核员");
        TASK_USER_GROUP_MAP.put(BANK_CREDIT_RECORD.getCode(), "银行征信员");
        TASK_USER_GROUP_MAP.put(SOCIAL_CREDIT_RECORD.getCode(), "社会征信员");
        TASK_USER_GROUP_MAP.put(LOAN_APPLY.getCode(), "业务员");
        TASK_USER_GROUP_MAP.put(VISIT_VERIFY.getCode(), "业务员");
        TASK_USER_GROUP_MAP.put(TELEPHONE_VERIFY.getCode(), "电审员");
        TASK_USER_GROUP_MAP.put(INFO_SUPPLEMENT.getCode(), "业务员");

        TASK_USER_GROUP_MAP.put("usertask_car_insurance_record", "车辆保险");
        TASK_USER_GROUP_MAP.put("usertask_gps_install", "GPS安装");
        TASK_USER_GROUP_MAP.put("usertask_busi_examine", "业务审批");
        TASK_USER_GROUP_MAP.put("usertask_contract_print", "合同套打");
        TASK_USER_GROUP_MAP.put("usertask_license_mortgage", "上牌抵押");
        TASK_USER_GROUP_MAP.put("usertask_pay_approve", "放款审批");
        TASK_USER_GROUP_MAP.put("usertask_pay_process", "财务打款");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
