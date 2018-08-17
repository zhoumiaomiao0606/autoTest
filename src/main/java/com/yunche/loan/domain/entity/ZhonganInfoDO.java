package com.yunche.loan.domain.entity;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
@Data
public class ZhonganInfoDO {
    private Long id;

    private Long orderId;

    private String age;

    private String gender;

    private String mobileCity;

    private String mobileCommDuration;

    private String mobileCommSts;

    private String phoneidNameCheck;

    private String highRiskBehavior;

    private String rsnHighRisk;

    private String rsnLongOverdue;

    private String rsnMultiLoan;

    private String rsnPolicyRestrict;

    private String rsnRiskRec;

    private String rspLawsuitAlllist;

    private String rspSpeclistInblacklist;

    private String rspSpeclistMaxdftlevel;

    private String rspWatchlistDetail;

    private String highRiskRecord;

    private String customerName;

    private List<ZhonganOverdueDO> overDueList = Lists.newArrayList();
    private List<RspCreditDO> creditList = Lists.newArrayList();
    private List<RspLawsuitDO> lawSuitList = Lists.newArrayList();
}