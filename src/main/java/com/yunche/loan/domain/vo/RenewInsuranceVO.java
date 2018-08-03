package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.RenewInsuranceDO;
import lombok.Data;

@Data
public class RenewInsuranceVO extends RenewInsuranceDO {

    private String sendee;
}
