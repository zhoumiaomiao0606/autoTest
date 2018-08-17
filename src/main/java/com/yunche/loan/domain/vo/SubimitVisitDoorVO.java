package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class SubimitVisitDoorVO {
    private String order_id;
    private String customer_name;
    private String customer_id_card;
    private String car_type;
    private String vehicle_apply_license_plate_area;
    private String financial_bank;
    private String partner_name;
    private String salesman_name;
}
