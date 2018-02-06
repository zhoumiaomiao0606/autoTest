package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class UserGroupQuery extends BaseQuery {
    private String name;

    private Long departmentId;
    /**
     * 部门ID列表
     */
    private List<Long> departmentIdList;

    private Long areaId;
}
