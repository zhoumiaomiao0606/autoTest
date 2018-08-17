package com.yunche.loan.domain.entity;

import java.util.Date;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDispatchedStaffId() {
        return dispatchedStaffId;
    }

    public void setDispatchedStaffId(Long dispatchedStaffId) {
        this.dispatchedStaffId = dispatchedStaffId;
    }

    public String getDispatchedStaffName() {
        return dispatchedStaffName;
    }

    public void setDispatchedStaffName(String dispatchedStaffName) {
        this.dispatchedStaffName = dispatchedStaffName == null ? null : dispatchedStaffName.trim();
    }

    public Date getDispatchedDate() {
        return dispatchedDate;
    }

    public void setDispatchedDate(Date dispatchedDate) {
        this.dispatchedDate = dispatchedDate;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    public String getVisitPeopleName() {
        return visitPeopleName;
    }

    public void setVisitPeopleName(String visitPeopleName) {
        this.visitPeopleName = visitPeopleName == null ? null : visitPeopleName.trim();
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic == null ? null : traffic.trim();
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack == null ? null : feedBack.trim();
    }

    public String getVisitAddress() {
        return visitAddress;
    }

    public void setVisitAddress(String visitAddress) {
        this.visitAddress = visitAddress == null ? null : visitAddress.trim();
    }

    public String getVisitDetail() {
        return visitDetail;
    }

    public void setVisitDetail(String visitDetail) {
        this.visitDetail = visitDetail == null ? null : visitDetail.trim();
    }

    public String getGpsDetail() {
        return gpsDetail;
    }

    public void setGpsDetail(String gpsDetail) {
        this.gpsDetail = gpsDetail == null ? null : gpsDetail.trim();
    }

    public String getPeopleDetail() {
        return peopleDetail;
    }

    public void setPeopleDetail(String peopleDetail) {
        this.peopleDetail = peopleDetail == null ? null : peopleDetail.trim();
    }

    public String getNextPlan() {
        return nextPlan;
    }

    public void setNextPlan(String nextPlan) {
        this.nextPlan = nextPlan == null ? null : nextPlan.trim();
    }

    public String getVisitResult() {
        return visitResult;
    }

    public void setVisitResult(String visitResult) {
        this.visitResult = visitResult == null ? null : visitResult.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public String getCarDetail() {
        return carDetail;
    }

    public void setCarDetail(String carDetail) {
        this.carDetail = carDetail == null ? null : carDetail.trim();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getVisitPeopleId() {
        return visitPeopleId;
    }

    public void setVisitPeopleId(Long visitPeopleId) {
        this.visitPeopleId = visitPeopleId;
    }
}