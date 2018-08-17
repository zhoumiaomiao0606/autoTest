package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class VehicleHandleDO {
    private Long orderid;

    private Long handdlePerson;

    private Date trailVehicleDate;

    private String trailVehicleAddress;

    private Date vehicleInboundDate;

    private String vehicleInboundAddress;

    private String vehicleInboundAddressDetail;

    private String customerCondition;

    private String chassisNumber;

    private String driverKilometers;

    private String vehichleSurface;

    private Byte vehicleKey;

    private String vehicleOthermaterial;

    private String vehicleOtherthings;

    private BigDecimal purchaseTax;

    private BigDecimal roadMaintenanceCosts;

    private String roadMaintenanceCostsStatement;

    private BigDecimal breakRulesCosts;

    private String breakRulesCostsStatement;

    private String vehicleStatement;

    private BigDecimal trailVehicleCosts;

    private BigDecimal vehicleMaintenanceCosts;

    private BigDecimal otherCosts;

    private BigDecimal finalCosts;

    private String relationMaterialUrl1;

    private String relationMaterialUrl2;

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Long getHanddlePerson() {
        return handdlePerson;
    }

    public void setHanddlePerson(Long handdlePerson) {
        this.handdlePerson = handdlePerson;
    }

    public Date getTrailVehicleDate() {
        return trailVehicleDate;
    }

    public void setTrailVehicleDate(Date trailVehicleDate) {
        this.trailVehicleDate = trailVehicleDate;
    }

    public String getTrailVehicleAddress() {
        return trailVehicleAddress;
    }

    public void setTrailVehicleAddress(String trailVehicleAddress) {
        this.trailVehicleAddress = trailVehicleAddress == null ? null : trailVehicleAddress.trim();
    }

    public Date getVehicleInboundDate() {
        return vehicleInboundDate;
    }

    public void setVehicleInboundDate(Date vehicleInboundDate) {
        this.vehicleInboundDate = vehicleInboundDate;
    }

    public String getVehicleInboundAddress() {
        return vehicleInboundAddress;
    }

    public void setVehicleInboundAddress(String vehicleInboundAddress) {
        this.vehicleInboundAddress = vehicleInboundAddress == null ? null : vehicleInboundAddress.trim();
    }

    public String getVehicleInboundAddressDetail() {
        return vehicleInboundAddressDetail;
    }

    public void setVehicleInboundAddressDetail(String vehicleInboundAddressDetail) {
        this.vehicleInboundAddressDetail = vehicleInboundAddressDetail == null ? null : vehicleInboundAddressDetail.trim();
    }

    public String getCustomerCondition() {
        return customerCondition;
    }

    public void setCustomerCondition(String customerCondition) {
        this.customerCondition = customerCondition == null ? null : customerCondition.trim();
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber == null ? null : chassisNumber.trim();
    }

    public String getDriverKilometers() {
        return driverKilometers;
    }

    public void setDriverKilometers(String driverKilometers) {
        this.driverKilometers = driverKilometers == null ? null : driverKilometers.trim();
    }

    public String getVehichleSurface() {
        return vehichleSurface;
    }

    public void setVehichleSurface(String vehichleSurface) {
        this.vehichleSurface = vehichleSurface == null ? null : vehichleSurface.trim();
    }

    public Byte getVehicleKey() {
        return vehicleKey;
    }

    public void setVehicleKey(Byte vehicleKey) {
        this.vehicleKey = vehicleKey;
    }

    public String getVehicleOthermaterial() {
        return vehicleOthermaterial;
    }

    public void setVehicleOthermaterial(String vehicleOthermaterial) {
        this.vehicleOthermaterial = vehicleOthermaterial == null ? null : vehicleOthermaterial.trim();
    }

    public String getVehicleOtherthings() {
        return vehicleOtherthings;
    }

    public void setVehicleOtherthings(String vehicleOtherthings) {
        this.vehicleOtherthings = vehicleOtherthings == null ? null : vehicleOtherthings.trim();
    }

    public BigDecimal getPurchaseTax() {
        return purchaseTax;
    }

    public void setPurchaseTax(BigDecimal purchaseTax) {
        this.purchaseTax = purchaseTax;
    }

    public BigDecimal getRoadMaintenanceCosts() {
        return roadMaintenanceCosts;
    }

    public void setRoadMaintenanceCosts(BigDecimal roadMaintenanceCosts) {
        this.roadMaintenanceCosts = roadMaintenanceCosts;
    }

    public String getRoadMaintenanceCostsStatement() {
        return roadMaintenanceCostsStatement;
    }

    public void setRoadMaintenanceCostsStatement(String roadMaintenanceCostsStatement) {
        this.roadMaintenanceCostsStatement = roadMaintenanceCostsStatement == null ? null : roadMaintenanceCostsStatement.trim();
    }

    public BigDecimal getBreakRulesCosts() {
        return breakRulesCosts;
    }

    public void setBreakRulesCosts(BigDecimal breakRulesCosts) {
        this.breakRulesCosts = breakRulesCosts;
    }

    public String getBreakRulesCostsStatement() {
        return breakRulesCostsStatement;
    }

    public void setBreakRulesCostsStatement(String breakRulesCostsStatement) {
        this.breakRulesCostsStatement = breakRulesCostsStatement == null ? null : breakRulesCostsStatement.trim();
    }

    public String getVehicleStatement() {
        return vehicleStatement;
    }

    public void setVehicleStatement(String vehicleStatement) {
        this.vehicleStatement = vehicleStatement == null ? null : vehicleStatement.trim();
    }

    public BigDecimal getTrailVehicleCosts() {
        return trailVehicleCosts;
    }

    public void setTrailVehicleCosts(BigDecimal trailVehicleCosts) {
        this.trailVehicleCosts = trailVehicleCosts;
    }

    public BigDecimal getVehicleMaintenanceCosts() {
        return vehicleMaintenanceCosts;
    }

    public void setVehicleMaintenanceCosts(BigDecimal vehicleMaintenanceCosts) {
        this.vehicleMaintenanceCosts = vehicleMaintenanceCosts;
    }

    public BigDecimal getOtherCosts() {
        return otherCosts;
    }

    public void setOtherCosts(BigDecimal otherCosts) {
        this.otherCosts = otherCosts;
    }

    public BigDecimal getFinalCosts() {
        return finalCosts;
    }

    public void setFinalCosts(BigDecimal finalCosts) {
        this.finalCosts = finalCosts;
    }

    public String getRelationMaterialUrl1() {
        return relationMaterialUrl1;
    }

    public void setRelationMaterialUrl1(String relationMaterialUrl1) {
        this.relationMaterialUrl1 = relationMaterialUrl1 == null ? null : relationMaterialUrl1.trim();
    }

    public String getRelationMaterialUrl2() {
        return relationMaterialUrl2;
    }

    public void setRelationMaterialUrl2(String relationMaterialUrl2) {
        this.relationMaterialUrl2 = relationMaterialUrl2 == null ? null : relationMaterialUrl2.trim();
    }
}