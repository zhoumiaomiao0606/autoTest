package com.yunche.loan.config.feign.response;

import lombok.Data;

@Data
public class ApplyCreditResponse {
    private String icbcApiRetcode;
    private String icbcApiRetmsg;
    private String icbcApiTimestamp;
    private String returnCode;
    private String returnMsg;
}
