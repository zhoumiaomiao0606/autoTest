package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class MaterialUpdateParam {

    @NotBlank
    private String order_id;
    /**
     * 资料齐全日期
     */
    private String complete_material_date;
    /**
     * 资料审核备注
     */
    private String remark;
    /**
     * 合同编号
     */
    private String contractNum;
}
