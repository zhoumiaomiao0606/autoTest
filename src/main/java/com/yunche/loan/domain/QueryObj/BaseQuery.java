package com.yunche.loan.domain.QueryObj;

import lombok.Data;

import java.util.Date;

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

    /**
     * 当前页数  默认值：1
     */
    private Integer pageIndex = 1;
    /**
     * 页面大小  默认值：10
     */
    private Integer pageSize = 10;

    /**
     * 状态（0：有效; 1：无效;）  默认值：0
     */
    private Byte status = VALID_STATUS;

    /**
     * 创建开始时间
     */
    private Date gmtCreateStart;
    /**
     * 创建截止时间
     */
    private Date gmtCreateEnd;
    /**
     * 修改开始时间
     */
    private Date gmtModifyStart;
    /**
     * 修改截止数据
     */
    private Date gmtModifyEnd;


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

