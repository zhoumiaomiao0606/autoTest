package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FinancialSchemeModifyUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;

import java.util.List;

public interface FinancialSchemeService {
    RecombinationVO detail(Long orderId);

    RecombinationVO verifyDetail(Long orderId, Long hisId);

    RecombinationVO modifyDetail(Long orderId, Long hisId);

    ResultBean<Long> modifyUpdate(FinancialSchemeModifyUpdateParam param);

    void migration(Long orderId, Long hisId, String action);

    List<UniversalCustomerOrderVO> queryModifyCustomerOrder(String name);
}
