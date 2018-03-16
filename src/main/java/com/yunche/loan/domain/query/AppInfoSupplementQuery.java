package com.yunche.loan.domain.query;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class AppInfoSupplementQuery extends BaseQuery {
    /**
     * 客户姓名
     */
    private String customerName;
}
