package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class FinanceErrVO {

    private String serialNo;
    private String  orderId;

    private String  businessDate;

    private String  userName;

    private String idCard;


    private  String  bankName;


    private String carType;

    private String partnerName;

    private String status;

    private String reason;

    private String processId;
    private String taskDefinitionKey;
}
