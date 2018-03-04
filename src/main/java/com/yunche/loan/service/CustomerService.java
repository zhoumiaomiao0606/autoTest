package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustDetailParam;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.CustRelaPersonInfoVO;
import com.yunche.loan.domain.viewObj.CustDetailVO;
import com.yunche.loan.domain.viewObj.CustomerVO;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface CustomerService {


    ResultBean<Long> createMainCust(CustBaseInfoVO custBaseInfoVO);

    ResultBean<Long> updateMainCust(CustBaseInfoVO custBaseInfoVO);

    ResultBean<Long> createRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO);

    ResultBean<Long> updateRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO);

    ResultBean<Void> deleteRelaCust(Long custId);

    ResultBean<Void> faceOff(String orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<CustDetailVO> detailAll(String orderId);

    ResultBean<Long> updateAll(CustDetailParam custDetailParam);

    ResultBean<Long> create(String orderId, CustomerVO customerVO);

    ResultBean<Long> update(CustomerVO customerVO);

    ResultBean<CustomerVO> getById(Long id);
}
