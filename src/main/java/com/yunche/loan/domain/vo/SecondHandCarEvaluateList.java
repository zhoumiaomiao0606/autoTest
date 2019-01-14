package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SecondHandCarEvaluateList
{
    private Long id;

    private String vin;

    /*private Long parnter_id;*/

    private String parnter_name;

   /* private Long saleman_id;*/

    private String saleman_name;

    private String owner;

    private String trimId;

    private String style_color;

    private String make_name;//品牌

    private String model_name;//车系

    private String name;//车型

    private BigDecimal evaluate_price;

    private String mileage;

    private String city_name;

    private String area_id;

    private String  query_time;

}
