package com.yunche.loan.config.feign.response;

import lombok.Data;

@Data
public class ApplyStatusResponse {
    private String  retcode;
    private String  retmsg;
    private String  custname;
    private String  idno;
    private String  cardno;
    private String  divitype;
    private String  loandate;
    private String  loanbrno;
    private String  accno;
    private String  amount;
    private String  term;
    private String  status;
}
