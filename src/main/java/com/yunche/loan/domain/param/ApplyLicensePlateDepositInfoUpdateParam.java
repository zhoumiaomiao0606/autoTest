package com.yunche.loan.domain.param;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class ApplyLicensePlateDepositInfoUpdateParam{
    @NotBlank
    private String order_id;//订单号
    @NotBlank
    private String apply_license_plate_deposit_date;//抵押日期

    private String license_plate_number;

    private String transfer_ownership_date;

    private String apply_license_plate_area;

    private String apply_license_plate_date;
    @NotBlank
    private String registration_certificate_number;

}
