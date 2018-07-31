package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.RenewInsuranceDO;
import lombok.Data;

@Data
public class RenewDetailVO extends RenewInsuranceDO{

    private String customerName;

    private String mobile;

}
