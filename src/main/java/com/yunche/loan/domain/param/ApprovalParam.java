package com.yunche.loan.domain.param;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/2/28
 */
@Data
public class ApprovalParam {

    /**
     * 业务单ID
     */
    private Long orderId;
    /**
     * taskId
     */
    private Long taskId;
    /**
     * 任务key
     */
    private String taskDefinitionKey;
    /**
     * 保存参数中-初始传入的key值
     */
    private String originalTaskDefinitionKey;

    /**
     * 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补  / 4-新增任务  / 5-反审
     */
    private Byte action;
    /**
     * 保存参数中-初始传入的action值
     */
    private Byte originalAction;
    /**
     * 审核备注信息
     */
    private String info;
    /**
     * 是否为自动任务
     */
    private boolean autoTask = false;


    ///////////////////////////////////////////////////// Boolean //////////////////////////////////////////////////////
    /**
     * 是否需要记录日志
     * 默认：true
     */
    private boolean needLog = true;
    /**
     * 是否需要鉴权
     * 默认：true
     */
    private boolean checkPermission = true;
    /**
     * 是否需要推送
     * 默认：true
     */
    private boolean needPush = true;

    /**
     * 移动端通过订单号弃单
     * 默认：false
     */
    private Boolean cancelByOrderId = false;
    ///////////////////////////////////////////////////// Boolean //////////////////////////////////////////////////////


    ///////////////////////////////////////////////////// 资料增补 //////////////////////////////////////////////////////
    /**
     * 资料增补单ID
     */
    private Long supplementOrderId;
    /**
     * 资料增补类型(1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;6-垫款资料缺少;7-视频增补;)
     */
    private Byte supplementType;
    /**
     * 要求增补内容
     */
    private String supplementContent;
    /**
     * 增补说明
     */
    private String supplementInfo;
    ///////////////////////////////////////////////////// 资料增补 //////////////////////////////////////////////////////


    ///////////////////////////////////////////////////// 征信审核 //////////////////////////////////////////////////////
    /**
     * 征信审核结果
     */
    private Byte creditResult;
    /**
     * 征信审核结果备注
     */
    private String creditInfo;
    /**
     * 征信审核附加条件
     */
    private String addCondition;
    ///////////////////////////////////////////////////// 征信审核 //////////////////////////////////////////////////////


    /////////////////////////////////////////////////////// 代偿 ////////////////////////////////////////////////////////
    /**
     * 流程ID
     */
    private Long processId;
    /**
     * 选项
     */
    private String choice;
    /**
     * 银行批次号
     */
    private Long bankRepayImpRecordId;
    /////////////////////////////////////////////////////// 代偿 ////////////////////////////////////////////////////////


    public String getOriginalTaskDefinitionKey() {
        if (null != originalTaskDefinitionKey) {
            return originalTaskDefinitionKey;
        }

        this.originalTaskDefinitionKey = taskDefinitionKey;
        return originalTaskDefinitionKey;
    }

    public Byte getOriginalAction() {
        if (null != originalAction) {
            return originalAction;
        }

        this.originalAction = action;
        return originalAction;
    }
}
