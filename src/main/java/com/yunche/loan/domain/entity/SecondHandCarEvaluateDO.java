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

    private String issue_date;

    private String make_name;

    private String model_name;

    private String trimId;

    private String name;

    private String style_color;

    private BigDecimal evaluate_price;

    private Date query_time;

    private Byte state;

    private Long saleman_id;

    private String manufacturerGuidePrice;//新车指导价

    private String dateOfProduction;//出厂日期

    private String year;//年份

    private String manufacturerName;//精真估厂商名称

    private String evaluate_json;

    private String mileage;

    private String vehicle_type;//车辆属性

    private String city_id;//所在地城市

    private Long ocr_id;//识别id


    private String b2C_a_low;

    private String b2C_a_mid;

    private String b2C_a_up;



    //展示
    private EvaluatePrice b2CPrices;//商家销售价 B2C 价格 包含 abc 三个车况

    private EvaluatePrice c2BPrices;//收购价 C2B 价格 包含 abc 三个车况

    private String secondCarUserdTime;

}