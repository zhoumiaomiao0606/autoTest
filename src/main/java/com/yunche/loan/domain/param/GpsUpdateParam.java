package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class GpsUpdateParam {
    @NotBlank
    private String order_id;//订单号
    @NotBlank
    private String gps_number;//GPS编号

}
