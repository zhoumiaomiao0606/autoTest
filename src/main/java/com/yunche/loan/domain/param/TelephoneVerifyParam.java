package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class TelephoneVerifyParam {

    @NotBlank
    private String order_id;

    @NotBlank
    private String car_gps_num;

    @NotBlank
    private String car_key;

    private String financial_cash_deposit;

    private String financial_extra_fee;

}
