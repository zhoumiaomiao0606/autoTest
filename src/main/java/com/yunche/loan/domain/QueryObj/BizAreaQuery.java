package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Data
public class BizAreaQuery extends BaseQuery {

    private Long id;

    private String name;

    private Long parentId;

    private Long employeeId;

    private Long areaId;

    private Integer level;

}
