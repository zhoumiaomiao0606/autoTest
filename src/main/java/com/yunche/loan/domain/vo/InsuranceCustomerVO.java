package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InsuranceCustomerVO {
    private String order_id;//业务编号
    private String insurance_info_id;//保险信息id
    private String customer_id;//客户id
    private String cname;//主贷人姓名
    private String id_card;//身份证
    private String mobile;//手机
    private String ename;//业务员
    private String pname;//合伙人
    private String insurance_year;//保险年度
    private String issue_bills_date;//出单日期
    private String residential_address;//现居地址
    private String total_insurance_amount;//保险总金额
    private List<InsuranceRelevanceVO> insurance_relevance_list = new ArrayList<InsuranceRelevanceVO>();

}
