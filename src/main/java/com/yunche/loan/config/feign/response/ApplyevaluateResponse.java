package com.yunche.loan.config.feign.response;

import com.yunche.loan.config.feign.response.base.BasicResponse;
import lombok.Data;

@Data
public class ApplyevaluateResponse extends BasicResponse {
    private String retcode;
    private String retmsg;
}

