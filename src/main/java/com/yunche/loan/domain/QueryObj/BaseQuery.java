package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Data
public class BaseQuery {

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    public Integer getStartRow() {
        Integer startRow = pageSize * pageIndex - 1;
        return startRow;
    }

}

