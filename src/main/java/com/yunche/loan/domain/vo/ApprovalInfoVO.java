package com.yunche.loan.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 审核信息对象
 *
 * @author liuzhe
 * @date 2018/3/23
 */
@Data
public class ApprovalInfoVO implements Serializable {

    private static final long serialVersionUID = -7256824959782835363L;

    /**
     * 审核人ID
     */
    private Long userId;
    /**
     * 审核人名称
     */
    private String userName;
    /**
     * 审核结果
     */
    private Byte action;
    /**
     * 任务key
     */
    private String taskDefinitionKey;
    /**
     * 审核备注
     */
    private String info;
    /**
     * 操作日期
     */
    private Date approvalDate;


    /**
     * 预贷款额度档次：1 - 13W以下; 2 - 13至20W; 3 - 20W以上;
     */
    private Byte loanAmount;

    /**
     * 打回源头任务：从哪个节点打回的
     */
    private String rejectOriginTask;
    /**
     * 打回任务的：源头-目的TASK
     */
    private String rejectTaskOriginAndDest;


    /**
     *
     */
    private String originTaskKey;
    /**
     *
     */
    private String originTaskId;
    /**
     *
     */
    private String destTaskKey;
    /**
     *
     */
    private String destTaskId;

    ///////////////////////////////////////////////////--资料增补--///////////////////////////////////////////////////////

    /**
     * 资料增补类型
     */
    private Byte infoSupplementType;
    /**
     * 资料增补内容
     */
    private String infoSupplementContent;
    /**
     * 资料增补说明
     */
    private String infoSupplementInfo;
    /**
     * 增补源头任务节点：从哪个节点发起的增补
     */
    private String infoSupplementOriginTask;

    ///////////////////////////////////////////////////--资料增补--///////////////////////////////////////////////////////

    public ApprovalInfoVO(Long userId, String userName, String taskDefinitionKey, Byte action, String info) {
        this.userId = userId;
        this.userName = userName;
        this.taskDefinitionKey = taskDefinitionKey;
        this.action = action;
        this.info = info;
        this.approvalDate = new Date();
    }
}
