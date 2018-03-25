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
     * 任务key
     */
    private String taskDefinitionKey;

    /**
     * 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补
     */
    private Byte action;
    /**
     * 审核备注信息
     */
    private String info;

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
    /**
     * 增补源头任务节点
     */
//    private String supplementOriginTask;
}
