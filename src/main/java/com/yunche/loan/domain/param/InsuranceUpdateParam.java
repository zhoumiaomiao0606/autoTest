package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@Data
public class InsuranceUpdateParam {
    @NotBlank
    private String order_id;//订单号
    @Valid
    @NotEmpty
    private List<InsuranceRelevanceUpdateParam>  insurance_relevance_list; //保险列表
}
