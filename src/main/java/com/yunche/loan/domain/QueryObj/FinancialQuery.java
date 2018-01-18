package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@Data
public class FinancialQuery extends BaseQuery {
    private String bankName;

    private Byte bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Long areaId;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Byte getBizType() {
        return bizType;
    }

    public void setBizType(Byte bizType) {
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
