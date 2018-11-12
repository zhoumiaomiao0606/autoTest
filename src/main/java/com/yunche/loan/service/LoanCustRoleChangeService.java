package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.LoanCustRoleChangeHisDetailVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.TaskListVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/11/12
 */
public interface LoanCustRoleChangeService {

    List<UniversalCustomerOrderVO> queryRoleChangeOrder(String name);

    RecombinationVO editDetail(Long orderId);

    Void editSave(Long orderId, List<CustomerParam> customers);

    ResultBean<List<TaskListVO>> queryHisList(TaskListQuery taskListQuery);

    LoanCustRoleChangeHisDetailVO hisDetail(Long roleChangeId);
}
