package com.yunche.loan.domain.QueryObj;

import lombok.Data;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@Data
public class FinancialQuery extends BaseQuery {
    private String bankName;

    private Byte bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Long areaId;

}
