package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2019/1/9
 */
@Data
public class OrderListVO {

    private String orderId;

    private String custId;

    private String custName;

    private String custIdCard;

    private String partnerId;

    private String partnerName;

    private String partnerCode;

    private Long bankId;

    private String bankName;
    /**
     * 当前查询节点KEY
     */
    private String taskKey;
    /**
     * 节点状态：1-已提交；2-待处理；3-已打回；
     */
    private String taskStatus;


    // ---------------------任务领取
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 任务领取人ID
     */
    private String taskReceiverId;
    /**
     * 任务领取人name
     */
    private String taskReceiverName;
    /**
     * 任务领取状态：1-未领取；2-已领取；
     */
    private String taskDisStatus;
}
