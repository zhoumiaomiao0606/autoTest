package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Data
public class BaseQuery {

    /**
     * 单页最大记录数
     */
    private static final Integer PAGE_SIZE_MAX = 50;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    public Integer getStartRow() {
        Integer startRow = (pageIndex - 1) * pageSize;
        return startRow;
    }

    public Integer getPageSize() {
        if (pageSize > PAGE_SIZE_MAX) {
            return PAGE_SIZE_MAX;
        }
        return pageSize;
    }

}

