package com.yunche.loan.config.feign.response.base;

import lombok.Data;

@Data
public class BasicResponse {
    private String icbcApiRetcode;
    private String icbcApiRetmsg;
    private String icbcApiTimestamp;
    private String returnCode;
    private String returnMsg;

    private String status;
    private String notes;
    private String updatetime;
    private String cardno;

    private String custname;
    private String idno;
    private String divitype;
    private String loandate;
    private String loanbrno;
    private String accno;
    private String amount;
    private String term;
}
