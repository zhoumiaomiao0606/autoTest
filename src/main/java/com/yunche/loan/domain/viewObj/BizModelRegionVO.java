package com.yunche.loan.domain.viewObj;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.dataObj.UserGroupDO;

import java.util.List;

public class BizModelRegionVO {

    private Long bizId;

    private Long areaId;

    private String prov;

    private Long provId;

    private String city;

    private Long cityId;

    private List<UserGroupVO> userGroupVOList = Lists.newArrayList();

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Long getProvId() {
        return provId;
    }

    public void setProvId(Long provId) {
        this.provId = provId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

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

    public List<UserGroupVO> getUserGroupVOList() {
        return userGroupVOList;
    }

    public void setUserGroupVOList(List<UserGroupVO> userGroupVOList) {
        this.userGroupVOList = userGroupVOList;
    }
}
