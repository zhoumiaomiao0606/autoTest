package com.yunche.loan.domain.dataObj;

import lombok.Data;

import java.util.Date;
@Data
public class BizModelDO {
    private Long bizId;

    private String title;

    private String description;

    private String scene;

    private String custTarget;

    private Byte carType;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene == null ? null : scene.trim();
    }

    public String getCustTarget() {
        return custTarget;
    }

    public void setCustTarget(String custTarget) {
        this.custTarget = custTarget == null ? null : custTarget.trim();
    }

    public Byte getCarType() {
        return carType;
    }

    public void setCarType(Byte carType) {
        this.carType = carType;
    }

    public Byte getStatus() {
        return status == null ? 0 : status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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