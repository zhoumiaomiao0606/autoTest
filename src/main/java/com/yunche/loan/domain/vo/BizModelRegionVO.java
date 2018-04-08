package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class BizModelRegionVO {

    private Long bizId;

    private Long areaId;

    private String prov;

    private Long provId;

    private String city;

    private Long cityId;

    private List<PartnerVO> partnerVOList = Lists.newArrayList();
}
