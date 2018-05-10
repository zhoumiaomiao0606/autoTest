package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class CollectionRecordUpdateParam {
    private String id;
    @NotBlank
    private String order_id;
    @NotBlank
    private String collection_date;
    @NotBlank
    private String collection_man_id;
    @NotBlank
    private String is_repayment;

    private String repayment_date;

    private String permit_repayment_date;
    @NotBlank
    private String cause;
    @NotBlank
    private String remark;
}
