package com.yunche.loan.service;

import com.yunche.loan.domain.param.RelaOrderCustomerParam;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/18
 */
public interface LoanApplyService {

    List<UniversalCustomerOrderVO> queryLoanApplyCustomerOrder(String name);

    void relaOrderCustomer(RelaOrderCustomerParam relaOrderCustomerParam);
}
