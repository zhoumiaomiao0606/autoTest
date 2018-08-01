package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class BankInterfaceSerialReturnVO {
    private String api_status;

    private String api_msg;

    private String status;

    private String reject_reason;

    private String request_time;

    private String callback_time;

    private String file_num;

    private String trans_code;

}
