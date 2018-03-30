package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class MaterialUpdateParam {
    @NotBlank
    private String order_id;
    @NotBlank
    private String complete_material_date;// 牌证齐全日期
}
