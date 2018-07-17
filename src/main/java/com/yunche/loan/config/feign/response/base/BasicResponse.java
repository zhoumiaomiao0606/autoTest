package com.yunche.loan.config.feign.response.base;

import lombok.Data;

@Data
public class BasicResponse {
    private String icbcApiRetcode;
    private String icbcApiRetmsg;
    private String icbcApiTimestamp;
    private String returnCode;
    private String returnMsg;
    private String apiMsg;
}
