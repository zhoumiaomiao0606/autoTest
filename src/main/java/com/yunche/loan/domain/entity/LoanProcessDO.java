package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessDO {

    private Long orderId;

    private Byte creditApply;

    private Byte bankCreditRecord;

    private Byte socialCreditRecord;

    private Byte loanApply;

    private Byte visitVerify;

    private Byte telephoneVerify;

    private Byte financialScheme;

    private Byte bankCardRecord;

    private Byte carInsurance;

    private Byte vehicleInformation;

    private Byte applyLicensePlateDepositInfo;

    private Byte installGps;

    private Byte commitKey;

    private Byte materialReview;

    private Byte materialPrintReview;

    private Byte businessReview;

    private Byte loanReview;

    private Byte remitReview;

    private Byte bankLendRecord;

    private Date gmtCreate;

    private Date gmtModify;
    /**
     * 弃单任务节点KEY
     */
    private String cancelTaskDefKey;
    /**
     * 贷款申请打回来源任务节点KEY（仅当loan_apply节点状态为3时可能[由资料审核打回]有值）
     */
    private String loanApplyRejectOrginTask;
    /**
     * 当前订单状态(1:进行中;2:已完结;3:已弃单;)
     */
    private Byte orderStatus;
}