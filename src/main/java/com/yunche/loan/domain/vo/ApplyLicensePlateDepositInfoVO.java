package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.BaseAreaDO;
import lombok.Data;

@Data
public class ApplyLicensePlateDepositInfoVO {
    private String order_id;
    private String customer_id;
    private String cname;
    private String card_type;
    private String id_card;
    private String ename;
    private String pname;
    private String old_driving_license_owner;
    private String car_type;
    private String bank;
    private String license_plate_number;
    private String transfer_ownership_date;
    private String apply_license_plate_area;
    private String apply_license_plate_date;
    private String apply_license_plate_deposit_date;
    private String registration_certificate_number;
    private BaseAreaDO hasApplyLicensePlateArea;
}
