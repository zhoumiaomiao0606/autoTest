package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;
@Data
public class VisitDoorDO {
    private Long id;

    private Long dispatchedStaffId;

    private String dispatchedStaffName;

    private Date dispatchedDate;

    private Long receiverId;

    private String receiverName;

    private String visitPeopleName;

    private Date visitDate;

    private String traffic;

    private String feedBack;

    private String visitAddress;

    private String visitDetail;

    private String gpsDetail;

    private String peopleDetail;

    private String nextPlan;

    private String visitResult;

    private String remarks;

    private String carDetail;

    private Long orderId;

    private Long visitPeopleId;

}