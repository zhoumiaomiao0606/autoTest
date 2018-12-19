package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ContractOverDueDetailVO
{
    private ContractOverDueCustomerInfoVO contractOverDueCustomerInfoVO;

    private FinancialSchemeVO financialSchemeVO;

    private VehicleInfoVO vehicleInfoVO;

    private List<UniversalCustomerVO> customers;
}
