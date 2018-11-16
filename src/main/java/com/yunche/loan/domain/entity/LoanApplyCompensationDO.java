package com.yunche.loan.domain.entity;

import com.yunche.loan.config.util.DateUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanApplyCompensationDO {

    private Long id;

    private Long orderId;

    //逾期金额
    private BigDecimal currArrears;

    private BigDecimal loanBanlance;

    private BigDecimal advancesBanlance;

    private Integer overdueDays;

    private Integer overdueNumber;

    private Integer advancesNumber;

    private BigDecimal riskTakingRatio;
    private BigDecimal compensationInterest;//代偿利息
    //财务代偿金额
    private BigDecimal compensationAmount;

    private String compensationCause;

    //申请代偿时间
    private Date applyCompensationDate;

    /**
     * （公司）打款账户详情关联ID
     */
    private Long refundApplyAccountId;

    private String receiveBank;

    private String receiveCarNumber;

    private String receiveAccount;

    private Date reviewDate;

    private String reviewOperator;

    private String partnerCompensationOperator;

    private Date partnerOperationDate;

    private BigDecimal partnerCompensationAmount;

    private String partnerDcReviewOperator;

    // 合伙人代偿确认经办时间
    private Date partnerDcReviewDate;

    private Byte status;

    private String remark;

    private Date gmtCreate;

    private Date gmtModify;

    /**
     * 日期去掉时分秒
     *
     * @param applyCompensationDate
     */
    @jdk.nashorn.internal.objects.annotations.Setter
    public void setApplyCompensationDate(Date applyCompensationDate) {
        Date applyCompDate = DateUtil.getDateTo10(applyCompensationDate);
        this.applyCompensationDate = applyCompDate;
    }
}