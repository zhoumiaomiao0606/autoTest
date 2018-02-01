package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@Data
public class FinancialQuery extends BaseQuery {
    private String bankName;

    private Integer bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Long areaId;

    private String prov;

    private String city;

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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getCategorySuperior() {
        return categorySuperior;
    }

    public void setCategorySuperior(String categorySuperior) {
        this.categorySuperior = categorySuperior;
    }

    public String getCategoryJunior() {
        return categoryJunior;
    }

    public void setCategoryJunior(String categoryJunior) {
        this.categoryJunior = categoryJunior;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
}
