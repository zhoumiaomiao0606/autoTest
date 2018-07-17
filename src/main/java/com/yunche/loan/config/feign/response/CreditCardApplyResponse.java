package com.yunche.loan.config.feign.response;

import com.yunche.loan.config.feign.response.base.BasicResponse;
import lombok.Data;

@Data
public class CreditCardApplyResponse extends BasicResponse {
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
