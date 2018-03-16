package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class MaterialUpdateParam {
    @NotBlank
    private String order_id;
    @NotBlank
    private String complete_material_date;// 牌证齐全日期
    @NotBlank
    private String rate_type;// 手续费收取方式 1 一次性 2 分期
    @NotBlank
    private String is_pledge;//  是否抵押 1 是 2 否
    @NotBlank
    private String is_guarantee;// 是否担保 1 是 2 否

}
