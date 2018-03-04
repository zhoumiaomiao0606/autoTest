package com.yunche.loan.domain.query;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Data
public class BizAreaQuery extends BaseQuery {

    /**
     *
     */
    private Long areaId;

    private String name;
    /**
     * 绑定的父级ID
     */
    private Long parentId;
    /**
     * 绑定的负责人ID
     */
    private Long employeeId;
    /**
     * 等级
     */
    private Integer level;
}
