package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class InsuranceUpdateParam {

    private Long orderId;//订单号

    private List<InsuranceInfoParam> InsuranceInfoList;

}
