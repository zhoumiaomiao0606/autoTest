package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Data
public class BaseQuery {
    /**
     * 单页最大记录数
     */
    private static final Integer PAGE_SIZE_MAX = 200;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 当前页数  默认值：1
     */
    private Integer pageIndex = 1;
    /**
     * 页面大小  默认值：10
     */
    private Integer pageSize = 10;
    /**
     * 状态（0：有效; 1：无效;）
     */
    private Byte status;
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

    /**
     * 分页开始行号
     *
     * @return
     */
    public Integer getStartRow() {
        Integer startRow = (pageIndex - 1) * pageSize;
        return startRow;
    }

    /**
     * 页面大小最大为：PAGE_SIZE_MAX
     *
     * @return
     */
    public Integer getPageSize() {
        if (pageSize > PAGE_SIZE_MAX) {
            return PAGE_SIZE_MAX;
        }
        return pageSize;
    }
}
