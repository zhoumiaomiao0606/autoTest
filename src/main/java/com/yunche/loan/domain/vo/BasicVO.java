package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class BasicVO
{
    private String Warranty;//保修期

    private String BrandType;//品牌类型

    private String Emission;//排放标准

    private String Country;//所属国别

    private String ProductionState;

    private String EndDate;//停产时间

    private String ModelID;//款式ID
}
