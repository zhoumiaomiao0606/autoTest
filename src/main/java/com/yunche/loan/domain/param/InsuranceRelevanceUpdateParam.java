package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InsuranceRelevanceUpdateParam {

    @NotBlank
    private String insuranceCompanyName;// 保险公司名称
    @NotBlank
    private String insuranceNumber;//  保单号
    @NotBlank
    private BigDecimal insuranceAmount;// 保险金额
    @NotBlank
    private Date startDate;// 开始日期
    @NotBlank
    private Date endDate;// 结束日期
    @NotBlank
    private Byte insuranceType;// 险种 1商业险 2交强险 3车船税
}
