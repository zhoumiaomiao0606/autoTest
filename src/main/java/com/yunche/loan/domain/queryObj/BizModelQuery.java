package com.yunche.loan.domain.queryObj;

import lombok.Data;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@Data
public class BizModelQuery extends BaseQuery {
    private String title;

    private String scene;

    private String custTarget;

    private Integer carType;

    private Long areaId;

    private String prov;

    private String city;

    private Long prodId;

    private String partnerName;

    private String partnerPhone;

    private List<Long> cascadeAreaIdList;
}
