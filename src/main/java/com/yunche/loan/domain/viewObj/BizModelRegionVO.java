package com.yunche.loan.domain.viewObj;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.dataObj.UserGroupDO;

import java.util.List;

public class BizModelRegionVO {

    private Long areaId;

    private String prov;

    private String city;

    private List<UserGroupDO> userGroupDOList = Lists.newArrayList();

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

    public List<UserGroupDO> getUserGroupDOList() {
        return userGroupDOList;
    }

    public void setUserGroupDOList(List<UserGroupDO> userGroupDOList) {
        this.userGroupDOList = userGroupDOList;
    }
}
