package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface LoanOrderService {

    ResultBean<InstLoanOrderDO> create(String processInstanceId);

    ResultBean<InstLoanOrderDO> update(InstLoanOrderVO instLoanOrderVO);

    ResultBean<List<InstLoanOrderVO>> queryOrderList(OrderListQuery orderListQuery);

    ResultBean<InstLoanOrderVO> detail(Long orderId);

    ResultBean<InstLoanOrderDO> getByProcInstId(String procInstId);

}
