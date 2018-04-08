package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BizModelVO {

    private Long bizId;

    private String title;

    private String description;

    private String scene;

    private String custTarget;

    private Integer carType;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    private List<BizModelRegionVO> bizModelRegionVOList;

    private List<FinancialProductVO> financialProductDOList;
}