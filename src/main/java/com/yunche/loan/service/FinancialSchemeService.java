package com.yunche.loan.service;

import com.yunche.loan.domain.param.FinancialSchemeModifyUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.domain.vo.UniversalHomeVisitInfoVO;

import java.util.List;

public interface FinancialSchemeService {
    public RecombinationVO detail(Long orderId);

    public RecombinationVO verifyDetail(Long orderId);

    public RecombinationVO modifyDetail(Long orderId);

    public void modifyUpdate(FinancialSchemeModifyUpdateParam param);

    public List<UniversalCustomerOrderVO> queryCustomerOrder(String name);
}
