package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@Data
public class InstallUpdateParam  {
    @NotBlank
    private String order_id;//订单号s
    @Valid
    @NotEmpty
    private List<GpsUpdateParam> gps_list;

    private String vehicleName;

    private String driverName;

    private String gpsCompany;
}
