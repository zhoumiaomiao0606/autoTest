package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CreditStructQueryCountDO {

    private Long id;

    private Integer loanQueryCountLastMonth1;

    private Integer creditQueryCountCardLastMonth1;

    private Integer oneselfQueryCountLastMonth1;

    private Integer loanQueryCountLastMonth2;

    private Integer creditQueryCountCardLastMonth2;

    private Integer oneselfQueryCountLastMonth2;

    private Integer loanQueryCountLastMonth6;

    private Integer creditQueryCountCardLastMonth6;

    private Integer oneselfQueryCountLastMonth6;

    private Date gmtCreate;

    private Date gmtModify;
}