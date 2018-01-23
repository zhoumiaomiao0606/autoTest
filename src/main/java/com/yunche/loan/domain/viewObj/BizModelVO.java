package com.yunche.loan.domain.viewObj;

import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.dataObj.UserGroupDO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BizModelVO {
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

    private List<BizModelRegionVO> bizModelRegionVOList;

    private List<BizRelaFinancialProductVO> financialProductDOList;

    public List<BizRelaFinancialProductVO> getFinancialProductDOList() {
        return financialProductDOList;
    }

    public void setFinancialProductDOList(List<BizRelaFinancialProductVO> financialProductDOList) {
        this.financialProductDOList = financialProductDOList;
    }

    public List<BizModelRegionVO> getBizModelRegionVOList() {
        return bizModelRegionVOList;
    }

    public void setBizModelRegionVOList(List<BizModelRegionVO> bizModelRegionVOList) {
        this.bizModelRegionVOList = bizModelRegionVOList;
    }

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
        return status;
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