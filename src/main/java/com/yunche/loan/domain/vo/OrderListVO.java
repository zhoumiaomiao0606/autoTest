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

    private String taskId;

    private String taskReceiverId;

    private String taskReceiverName;

    private String taskStatus;
}
