package com.yunche.loan.domain.vo;

import java.util.Date;
import java.util.List;

public class BizModelVO {
    private Long bizId;

    private String title;

    private String description;

    private String scene;

    private String custTarget;

    private Integer carType;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private List<BizModelRegionVO> bizModelRegionVOList;

    private List<BizRelaFinancialProductVO> financialProductDOList;

    public List<BizRelaFinancialProductVO> getFinancialProductDOList() {
        return financialProductDOList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BizModelVO that = (BizModelVO) o;

//        if (!bizId.equals(that.bizId)) return false;
//        if (!title.equals(that.title)) return false;
//        if (!scene.equals(that.scene)) return false;
//        if (!custTarget.equals(that.custTarget)) return false;
//        if (!carType.equals(that.carType)) return false;
        return bizId.equals(that.bizId);

    }

    @Override
    public int hashCode() {
        int result = bizId.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + scene.hashCode();
        result = 31 * result + custTarget.hashCode();
        result = 31 * result + carType.hashCode();
        result = 31 * result + status.hashCode();
        return result;
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

    public Integer getCarType() {
        return carType;
    }

    public void setCarType(Integer carType) {
        this.carType = carType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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