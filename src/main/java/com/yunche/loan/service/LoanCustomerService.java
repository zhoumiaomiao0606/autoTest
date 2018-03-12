package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.AllCustDetailParam;
import com.yunche.loan.domain.vo.CustDetailVO;
import com.yunche.loan.domain.vo.CustomerVO;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface LoanCustomerService {

    ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<CustDetailVO> detailAll(Long orderId);

    ResultBean<Long> updateAll(AllCustDetailParam allCustDetailParam);

    ResultBean<Long> create(LoanCustomerDO loanCustomerDO);

    ResultBean<Void> update(LoanCustomerDO loanCustomerDO);

    ResultBean<CustomerVO> getById(Long id);
}
