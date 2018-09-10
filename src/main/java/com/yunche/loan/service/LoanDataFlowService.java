package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.param.LoanDataFlowParam;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.domain.vo.UniversalDataFlowDetailVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
public interface LoanDataFlowService {

    LoanDataFlowDO getLastByOrderIdAndType(Long orderId, Byte oldType);

    ResultBean<UniversalDataFlowDetailVO> detail(Long id);

    ResultBean<Long> create(LoanDataFlowParam loanDataFlowParam);

    ResultBean<Integer> update(LoanDataFlowParam loanDataFlowParam);

    ResultBean<List<BaseVO>> flowDept();

    ResultBean<List<UniversalCustomerOrderVO>> queryDataFlowCustomerOrder(String customerName);

    ResultBean<String> export(TaskListQuery taskListQuery);

    ResultBean<Integer> imp(String ossKey);

    ResultBean<Integer> batchReceived(String ids);

    ResultBean<Long> getDataFlowId(Long orderId, String taskDefinitionKey);
}