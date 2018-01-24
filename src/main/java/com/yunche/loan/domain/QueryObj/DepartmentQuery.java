package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class DepartmentQuery extends BaseQuery {
    private Long id;

    private String name;

    private Long parentId;

    private Long employeeId;

    private String tel;

    private String fax;

    private Long areaId;

    private String address;

    private String openBank;

    private String receiveUnit;

    private String bankAccount;

    private Byte status;

    private String feature;
}
