package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class FirstCarSiteVO
{
    private String Brand;

    private String Series;

    private NewPriceVO NewPrice;

    private String Color;//车身颜色

    private ModelVO Model;

    private UsedPriceVO UsedPrice;

    private String GB;//国标码

    private String ProductionDate;//出厂日期

    private DetailVO Detail;

    private String Mfrs;

    private String EngineNo;//发动机号

}
