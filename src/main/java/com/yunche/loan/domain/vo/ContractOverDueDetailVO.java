package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.OverdueInterestDO;
import lombok.Data;

import java.util.List;

@Data
public class ContractOverDueDetailVO
{
    private ContractOverDueCustomerInfoVO contractOverDueCustomerInfoVO;

    private FinancialSchemeVO financialSchemeVO;

    private VehicleInfoVO vehicleInfoVO;

    private OverdueInterestDO overdueInterestDO;

    private List<UniversalCustomerVO> customers;
}
