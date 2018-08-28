package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @program: yunche-biz
 * @description: 消息
 * @author: Mr.WangGang
 * @create: 2018-08-28 14:44
 **/
@Data
public class FlowOperationMsgListVO {
    private String id;
    private String orderId;
    private String title;
    private String prompt;
    private String msg;
    private String sender;
    private String processKey;
    private String sendDate;
    private String chooseDate;
    private String readStatus;
    private String type;
    private String customerName;
    private String customerIdCard;
}
