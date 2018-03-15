package com.yunche.loan.domain.query;

import lombok.Data;

/**
 * APP端-客户查询
 *
 * @author liuzhe
 * @date 2018/3/14
 */
@Data
public class AppCustomerQuery extends BaseQuery {
    /**
     * 客户姓名
     */
    private String customerName;
    /**
     * 客户贷款状态：1-在贷客户;  2-已贷客户;
     */
    private Byte loanStatus;
}
