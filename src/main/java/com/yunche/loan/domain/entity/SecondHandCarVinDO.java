package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SecondHandCarVinDO {
    private Long id;

    private Long parnter_id;

    private String vin;//vin码

    private String plate_num;//车牌号

    private String vehicle_type;

    private String owner;//行驶证车主

    private String use_character;

    private String addr;

    private String model;

    private String engine_num;//发动机号

    private String register_date;//注册日期

    private String issue_date;

    private String appproved_passenger_capacity;

    private String approved_load;

    private String file_no;

    private String gross_mass;

    private String inspection_record;

    private String overall_dimension;

    private String traction_mass;

    private String unladen_mass;

    private Date query_time;

}