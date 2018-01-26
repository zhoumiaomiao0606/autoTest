package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class UserGroupQuery extends BaseQuery {
    private String name;

    private Long departmentId;

    private Long areaId;
}
