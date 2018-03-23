package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoanProcessDO {
    /**
     * 业务单ID
     */
    private Long orderId;
    /**
     * 征信申请
     */
    private Byte creditApply;
    /**
     * 征信申请审核
     */
    private Byte creditApplyVerify;
    /**
     * 银行征信录入
     */
    private Byte bankCreditRecord;
    /**
     * 社会征信录入
     */
    private Byte socialCreditRecord;
    /**
     * 业务申请
     */
    private Byte loanApply;
    /**
     * 上门调查
     */
    private Byte visitVerify;
    /**
     * 电审
     */
    private Byte telephoneVerify;
    /**
     * 资料增补
     */
    private Byte infoSupplement;
    /**
     * 车辆保险
     */
    private Byte carInsurance;
    /**
     * 上牌记录
     */
    private Byte applyLicensePlateRecord;
    /**
     * 上牌抵押
     */
    private Byte applyLicensePlateDepositInfo;
    /**
     * GPS安装
     */
    private Byte installGps;
    /**
     * 待收钥匙
     */
    private Byte commitKey;
    /**
     * 资料审核
     */
    private Byte materialReview;
    /**
     * 合同套打
     */
    private Byte materialPrintReview;
    /**
     * 业务审批
     */
    private Byte businessReview;
    /**
     * 放款审批
     */
    private Byte loanReview;
    /**
     * 打款确认
     */
    private Byte remitReview;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModify;
    /**
     * 弃单任务节点KEY
     */
    private String cancelTaskDefKey;
    /**
     * 贷款申请打回来源任务节点KEY（仅当loan_apply节点状态为3[打回]时有值）
     */
    private String loanApplyRejectOrginTask;
}