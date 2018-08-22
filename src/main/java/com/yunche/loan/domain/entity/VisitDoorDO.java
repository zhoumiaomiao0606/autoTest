package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;
@Data
public class VisitDoorDO {
    //主键
    private Long id;
    //派单员id
    private Long dispatchedStaffId;
    //派单员name
    private String dispatchedStaffName;
    //派单日期
    private Date dispatchedDate;
    //接受员id
    private Long receiverId;
    //接受员name
    private String receiverName;
    //上门人员
    private String visitPeopleName;
    //上门日期
    private Date visitDate;
    //交通方式
    private String traffic;
    //业务员反馈意见
    private String feedBack;
    //上门地址
    private String visitAddress;
    //上门详情
    private String visitDetail;
    //gps详情
    private String gpsDetail;
    //人详情
    private String peopleDetail;
    //下一步计划
    private String nextPlan;
    //上门结果
    private String visitResult;
    //备注
    private String remarks;
    //车详情
    private String carDetail;
    //业务单号
    private Long orderId;
    //上门人员id
    private Long visitPeopleId;
    //批次号id
    private Long bankRepayImpRecordId;

    private int status;
}