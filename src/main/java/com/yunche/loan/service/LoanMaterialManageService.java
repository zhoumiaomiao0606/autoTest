package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanMaterialManageDO;
import com.yunche.loan.domain.vo.LoanMaterialManageVO;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
public interface LoanMaterialManageService {

    ResultBean<Void> save(LoanMaterialManageDO loanMaterialManageDO);

    ResultBean<LoanMaterialManageVO> detail(Long orderId);
}
