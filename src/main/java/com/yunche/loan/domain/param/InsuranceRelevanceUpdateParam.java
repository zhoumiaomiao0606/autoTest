package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Data
public class InsuranceRelevanceUpdateParam {
    @NotBlank
    private String insurance_company_name;// 保险公司名称
    @NotBlank
    private String insurance_number;//  保单号
    @NotBlank
    private String insurance_amount;// 保险金额
    @NotBlank
    private String start_date;// 开始日期
    @NotBlank
    private String end_date;// 结束日期
    @NotBlank
    private String insurance_type;// 险种 1商业险 2交强险 3车船税
}
