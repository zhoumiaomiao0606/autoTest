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
    private String taskDefinitionKey_;

    /**
     * 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补  / 4-新增任务
     */
    private Byte action;
    /**
     * 审核备注信息
     */
    private String info;
    /**
     * 目标节点
     */
    private String target;


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
     * 增补类型：  1-电审资料增补;  2-资料审核增补;
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


    public String getTaskDefinitionKey_() {
        if (null != taskDefinitionKey_) {
            return taskDefinitionKey_;
        }

        this.taskDefinitionKey_ = taskDefinitionKey;
        return taskDefinitionKey;
    }
}
