package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class OverDueInterestVO extends ContractOverDueVO
{

    private String vehicleInfoOverDays;

    private String overInterest;

}
