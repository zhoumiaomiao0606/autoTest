package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ManualDistributionParam {


    @NotBlank
    private String order_id;
    @NotBlank
    private String sendee;

    private Long sendee_id;
}
