package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class UniversalInsuranceVO {

    Byte insuranceYear;//保险年度

    private List<InsuranceRelevanceDO> insuranceRele= Collections.EMPTY_LIST;


}
