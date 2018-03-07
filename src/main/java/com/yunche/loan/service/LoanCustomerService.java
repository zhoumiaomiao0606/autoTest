package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustDetailParam;
import com.yunche.loan.domain.vo.CustBaseInfoVO;
import com.yunche.loan.domain.vo.CustRelaPersonInfoVO;
import com.yunche.loan.domain.vo.CustDetailVO;
import com.yunche.loan.domain.vo.CustomerVO;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface LoanCustomerService {

    ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<CustDetailVO> detailAll(Long orderId);

    ResultBean<Long> updateAll(CustDetailParam custDetailParam);

    ResultBean<Long> create(CustomerVO customerVO);

    ResultBean<Void> update(CustomerVO customerVO);

    ResultBean<CustomerVO> getById(Long id);
}
