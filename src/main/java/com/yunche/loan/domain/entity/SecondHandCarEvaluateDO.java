package com.yunche.loan.domain.entity;

import com.yunche.loan.domain.vo.EvaluatePrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SecondHandCarEvaluateDO
{
    private Long id;

    private String vin;

    private Long parnter_id;

    private String owner;

    private String plate_num;

    private String engine_num;

    private Date register_date;

    private String make_name;

    private String model_name;

    private String trimId;

    private String name;

    private String style_color;

    private BigDecimal evaluate_price;

    private Date query_time;

    private Byte state;

    private Long saleman_id;

    private String manufacturerGuidePrice;

    private String dateOfProduction;

    private String year;

    private String manufacturerName;

    private String evaluate_json;

    private String mileage;

    //展示
    private EvaluatePrice b2CPrices;//商家销售价 B2C 价格 包含 abc 三个车况

    private EvaluatePrice c2BPrices;//收购价 C2B 价格 包含 abc 三个车况

}