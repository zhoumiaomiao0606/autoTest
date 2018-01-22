package com.yunche.loan.domain.QueryObj;

import lombok.Data;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

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

    /**
     * 状态  0：有效; 1：无效;
     * <p>
     * 默认值：0
     */
    private Byte status = VALID_STATUS;

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

