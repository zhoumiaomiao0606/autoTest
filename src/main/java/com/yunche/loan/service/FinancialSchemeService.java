package com.yunche.loan.service;

import com.yunche.loan.domain.param.FinancialSchemeModifyUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.domain.vo.UniversalHomeVisitInfoVO;

import java.util.List;

public interface FinancialSchemeService {
    public RecombinationVO detail(Long orderId);

    public RecombinationVO verifyDetail(Long orderId,Long hisId);

    public RecombinationVO modifyDetail(Long orderId,Long hisId);

    public void modifyUpdate(FinancialSchemeModifyUpdateParam param);

    public void migration(Long orderId,Long hisId,String action);

    public List<UniversalCustomerOrderVO> queryModifyCustomerOrder(String name);

    public List<UniversalCustomerOrderVO> queryRefundCustomerOrder(String name);
}
