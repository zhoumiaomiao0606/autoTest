package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RepaymentRecordVO {


    private String idCard;

    private String repayCardId;

    private Integer currentOverdueTimes;

    private Long bizOrder;

    private String userName;

    private BigDecimal optimalReturn;

    private BigDecimal minPayment;

    private BigDecimal pastDue;

    private Integer cumulativeOverdueTimes;

    private BigDecimal cardBalance;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    //实际贷款金额
    private BigDecimal loanAmount;

}
