package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class BankReturnParam {
    private String icbcApiRetcode;//icbcApiRetcode
    private String icbcApiRetmsg;//icbcApiRetmsg
    private String icbcApiTimestamp;//icbcApiTimestamp
    private  String returnCode;//
    private  String returnMsg;
}
