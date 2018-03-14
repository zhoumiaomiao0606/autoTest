package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class InsuranceRelevanceVO {

    private String insurance_info_id;//主表id
    private String insurance_company_name;//保险公司
    private String insurance_number;//保单号
    private String area;//保单地
    private String insurance_amount;//保险金额
    private String start_date;//开始日期
    private String end_date;//结束日期
    private String insurance_type;//险种 1商业险 2交强险 3车船税

}
