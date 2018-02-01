package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@Data
public class BizModelQuery extends BaseQuery {
    private String scene;

    private String custTarget;

    private Integer carType;

    private Long areaId;

    private String prov;

    private String city;

    private Long prodId;

    private String partnerName;

    private String partnerPhone;

    private List<Long> cascadeAreaIdList;

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Long> getCascadeAreaIdList() {
        return cascadeAreaIdList;
    }

    public void setCascadeAreaIdList(List<Long> cascadeAreaIdList) {
        this.cascadeAreaIdList = cascadeAreaIdList;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getCustTarget() {
        return custTarget;
    }

    public void setCustTarget(String custTarget) {
        this.custTarget = custTarget;
    }

    public Integer getCarType() {
        return carType;
    }

    public void setCarType(Integer carType) {
        this.carType = carType;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerPhone() {
        return partnerPhone;
    }

    public void setPartnerPhone(String partnerPhone) {
        this.partnerPhone = partnerPhone;
    }
}
