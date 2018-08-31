package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalBankInterfaceSerialVO {
    private String serialNo;

    private String customerId;

    private String apiStatus;

    private String status;

    private String requestTime;

    private String callbackTime;

    private String rejectReason;
}
