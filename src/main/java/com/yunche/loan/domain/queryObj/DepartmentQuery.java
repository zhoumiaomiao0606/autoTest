package com.yunche.loan.domain.queryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class DepartmentQuery extends BaseQuery {

    private String name;

    private Long parentId;

    private Long leaderId;

    private Long areaId;

    private Integer level;

    private String tel;

    private String fax;

    private String address;

    private String openBank;

    private String receiveUnit;

    private String bankAccount;

    private String feature;

    /**
     * 用户组ID
     */
    private Long userGroupId;
}
