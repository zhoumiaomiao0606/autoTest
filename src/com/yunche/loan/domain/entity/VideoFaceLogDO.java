package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class VideoFaceLogDO {
    private Long id;

    private Long orderId;

    private Long guaranteeCompanyId;

    private String guaranteeCompanyName;

    private Long customerId;

    private String customerName;

    private String customerIdCard;

    private String path;

    private Boolean type;

    private Long auditorId;

    private String auditorName;

    private Boolean action;

    private String latlon;

    private String location;

    private Long carDetailId;

    private String carDetailName;

    private BigDecimal carPrice;

    private BigDecimal expectLoanAmount;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getGuaranteeCompanyId() {
        return guaranteeCompanyId;
    }

    public void setGuaranteeCompanyId(Long guaranteeCompanyId) {
        this.guaranteeCompanyId = guaranteeCompanyId;
    }

    public String getGuaranteeCompanyName() {
        return guaranteeCompanyName;
    }

    public void setGuaranteeCompanyName(String guaranteeCompanyName) {
        this.guaranteeCompanyName = guaranteeCompanyName == null ? null : guaranteeCompanyName.trim();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName == null ? null : customerName.trim();
    }

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public void setCustomerIdCard(String customerIdCard) {
        this.customerIdCard = customerIdCard == null ? null : customerIdCard.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName == null ? null : auditorName.trim();
    }

    public Boolean getAction() {
        return action;
    }

    public void setAction(Boolean action) {
        this.action = action;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon == null ? null : latlon.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public Long getCarDetailId() {
        return carDetailId;
    }

    public void setCarDetailId(Long carDetailId) {
        this.carDetailId = carDetailId;
    }

    public String getCarDetailName() {
        return carDetailName;
    }

    public void setCarDetailName(String carDetailName) {
        this.carDetailName = carDetailName == null ? null : carDetailName.trim();
    }

    public BigDecimal getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(BigDecimal carPrice) {
        this.carPrice = carPrice;
    }

    public BigDecimal getExpectLoanAmount() {
        return expectLoanAmount;
    }

    public void setExpectLoanAmount(BigDecimal expectLoanAmount) {
        this.expectLoanAmount = expectLoanAmount;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}