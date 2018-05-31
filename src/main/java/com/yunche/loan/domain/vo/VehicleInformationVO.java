package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.BaseAreaDO;
import lombok.Data;

@Data
public class VehicleInformationVO {

    private String order_id;
    private String customer_id;
    private String cname;
    private String id_card;
    private String ename;
    private String pname;
    private String car_name;
    private String car_price;
    private String bank;
    private String bank_period_principal;
    private String loan_amount;
    private String assess_price;
    private String production_type;
    private String car_type;
    private String license_plate_type;
    private String color;
    private String qualified_certificate_number;
    private String register_date;
    private String vehicle_identification_number;
    private String displacement;
    private String license_plate_number;
    private String apply_license_plate_date;
    private String apply_license_plate_area;
    private String transfer_ownership_date;
    private String now_driving_license_owner;
    private String old_driving_license_owner;
    private String engine_number;
    private String registration_certificate_number;
    private String invoice_car_dealer;
    private String purchase_car_invoice_num;
    private String purchase_car_invoice_price;
    private String invoice_down_payment;
    private String purchase_car_invoice_date;
    private String retrieve_key;
    private String down_payment_money;
    private String first_register_date;
    private String customize_brand;
    private BaseAreaDO hasApplyLicensePlateArea;
}
