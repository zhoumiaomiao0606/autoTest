package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.UserGroupQuery;
import com.yunche.loan.domain.dataObj.UserGroupDO;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
public interface UserGroupService {
    ResultBean<Long> create(UserGroupParam userGroupParam);

    ResultBean<Void> update(UserGroupParam userGroupParam);

    ResultBean<Void> delete(Long id);

    ResultBean<UserGroupVO> getById(Long id);

    ResultBean<List<UserGroupVO>> query(UserGroupQuery query);

    ResultBean<List<AuthVO>> listAuth(BaseQuery query);

    ResultBean<List<EmployeeVO>> listEmployee(BaseQuery query);

    ResultBean<Void> bindEmployee(Long id, String employeeIds);

    ResultBean<Void> unbindEmployee(Long id, String employeeIds);

    ResultBean<Void> bindAuth(Long id, String authIds);

    ResultBean<Void> unbindAuth(Long id, String authIds);
}
