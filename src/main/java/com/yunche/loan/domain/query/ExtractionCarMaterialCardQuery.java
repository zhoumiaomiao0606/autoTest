/**
 * @author: ZhongMingxiao
 * @create: 2018-08-07 09:02
 * @description: 提车资料查询
 **/
package com.yunche.loan.domain.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExtractionCarMaterialCardQuery
{
    /**
     * 当前页数  默认值：1
     */
    @NotNull
    private Integer pageIndex = 1;
    /**
     * 页面大小  默认值：10
     */
    @NotNull
    private Integer pageSize = 10;
}
