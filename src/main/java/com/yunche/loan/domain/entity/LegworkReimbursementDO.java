package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LegworkReimbursementDO {

    private Long id;

    private String transFee;

    private String hotelFee;

    private String eatFee;

    private String busiFee;

    private String otherFee;

    private Date gmtCreateTime;

    private Date gmtUpdateTime;

    private Byte status;

    private String reimbursementAmount;

    /**
     * 公司退/打款账户详情关联ID
     */
    private Long refundApplyAccountId;

    private String remitAccount;

    private String collectionBank;

    private String collectionAccount;

    private String collectionAccountNumber;

    private Date busiTime;

    private Long applyUserId;

    private String applyUserName;

    private Long reviewUserId;

    private String reviewUserName;
}