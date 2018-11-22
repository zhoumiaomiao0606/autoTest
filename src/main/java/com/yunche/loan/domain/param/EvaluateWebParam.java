package com.yunche.loan.domain.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class EvaluateWebParam
{
    private String trimId;//车型 ID

    private Byte orgin=0; //来源  ---0-web端  --  1-app端

    private Long orderId;//订单id

    private String plate_num;//车牌号

    private String mileage;//里程



    //需要保存的
    private String vin;

    private String owner;//行驶证车主

    private String engine_num;//发动机号

    private String register_date;//注册日期----上牌时间

    private String make_name;//品牌

    private String model_name;//车系

    private String name;//车型

    private String style_color;//颜色


    //新加
    private String manufacturerGuidePrice;//新车指导价

    private String dateOfProduction;//出厂日期

    private String year;//出厂年份

    private String manufacturerName;//精真估厂商名称。

    private String vehicle_type;//车辆类型

    private String ocr_id;//识别id



    //后端处理
    private BigDecimal evaluate_price;//评估价

    private String query_time;//查询时间

    //需要更新原表记录的
    /*private String vehicle_type;//车辆类型*/
}
