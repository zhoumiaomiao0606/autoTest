package com.yunche.loan.domain.query;

import lombok.Data;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@Data
public class FinancialQuery extends BaseQuery {
    private String bankName;

    private Integer bizType;

    private String categorySuperior;

    private String categoryJunior;

    private String rate;

    private Long areaId;

    private String prov;

    private String city;

    private Byte status;

    private List<Long> cascadeAreaIdList;
}
