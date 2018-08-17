package com.yunche.loan.domain.entity;

import java.util.Date;

public class CollectionNewInfoDO {
    private Long id;

    private String isvisit;

    private String circumstances;

    private String police;

    private String notarization;

    private String secondMortgage;

    private String collectionManid;

    private String islaw;

    private String dispatchedStaff;

    private Date dispatchedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsvisit() {
        return isvisit;
    }

    public void setIsvisit(String isvisit) {
        this.isvisit = isvisit == null ? null : isvisit.trim();
    }

    public String getCircumstances() {
        return circumstances;
    }

    public void setCircumstances(String circumstances) {
        this.circumstances = circumstances == null ? null : circumstances.trim();
    }

    public String getPolice() {
        return police;
    }

    public void setPolice(String police) {
        this.police = police == null ? null : police.trim();
    }

    public String getNotarization() {
        return notarization;
    }

    public void setNotarization(String notarization) {
        this.notarization = notarization == null ? null : notarization.trim();
    }

    public String getSecondMortgage() {
        return secondMortgage;
    }

    public void setSecondMortgage(String secondMortgage) {
        this.secondMortgage = secondMortgage == null ? null : secondMortgage.trim();
    }

    public String getCollectionManid() {
        return collectionManid;
    }

    public void setCollectionManid(String collectionManid) {
        this.collectionManid = collectionManid == null ? null : collectionManid.trim();
    }

    public String getIslaw() {
        return islaw;
    }

    public void setIslaw(String islaw) {
        this.islaw = islaw == null ? null : islaw.trim();
    }

    public String getDispatchedStaff() {
        return dispatchedStaff;
    }

    public void setDispatchedStaff(String dispatchedStaff) {
        this.dispatchedStaff = dispatchedStaff == null ? null : dispatchedStaff.trim();
    }

    public Date getDispatchedDate() {
        return dispatchedDate;
    }

    public void setDispatchedDate(Date dispatchedDate) {
        this.dispatchedDate = dispatchedDate;
    }
}