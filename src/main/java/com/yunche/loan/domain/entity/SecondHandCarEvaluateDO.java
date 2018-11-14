package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SecondHandCarEvaluateDO {
    private Long id;

    private String vin;

    private Long parnter_id;

    private Long saleman_id;

    private String owner;

    private String plate_num;

    private String engine_num;

    private Date register_date;

    private String make_name;

    private String model_name;

    private String name;

    private String style_color;

    private BigDecimal evaluate_price;

    private Date query_time;

    private Byte state;

}