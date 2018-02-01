package com.yunche.loan.domain.queryObj;

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
