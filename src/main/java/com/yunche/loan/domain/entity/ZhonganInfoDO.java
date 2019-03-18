package com.yunche.loan.domain.entity;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
public class ZhonganInfoDO {
    private Long id;

    private String idCard;

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

    private Date createDate;

    private Long orderId;

    private String customerType;

    private String resultMessage;

    private String tel;

    private String highRiskRemind;

    private String lowRiskRemind;

    private String level;

    private String score;

    private List<ZhonganOverdueDO> overDueList = Lists.newArrayList();
    private List<RspCreditDO> creditList = Lists.newArrayList();
    private List<RspLawsuitDO> lawSuitList = Lists.newArrayList();

    private List<ZhonganCaipanDO> zhonganCaipanDOList = Lists.newArrayList();
    private List<ZhonganFeizhengDO> zhonganFeizhengDOList = Lists.newArrayList();
    private List<ZhonganQiankuanDO> zhonganQiankuanDOList = Lists.newArrayList();
    private List<ZhonganQianshuiDO> zhonganQianshuiDOList = Lists.newArrayList();
    private List<ZhonganShenpanDO> zhonganShenpanDOList = Lists.newArrayList();
    private List<ZhonganShixinDO> zhonganShixinDOList = Lists.newArrayList();
    private List<ZhonganWeifaDO> zhonganWeifaDOList = Lists.newArrayList();
    private List<ZhonganXianchuDO> zhonganXianchuDOList = Lists.newArrayList();
    private List<ZhonganXiangaoDO> zhonganXiangaoDOList = Lists.newArrayList();
    private List<ZhonganZhixingDO> zhonganZhixingDOList = Lists.newArrayList();
    private List<ZhonganZuifanDO> zhonganZuifanDOList = Lists.newArrayList();

    private String rspGongAn;
}