package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.UserGroupQuery;
import com.yunche.loan.domain.dataObj.UserGroupDO;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
public interface UserGroupService {
    ResultBean<Long> create(UserGroupParam userGroupParam);

    ResultBean<Void> update(UserGroupDO userGroupDO);

    ResultBean<Void> delete(Long id);

    ResultBean<UserGroupVO> getById(Long id);

    ResultBean<List<UserGroupVO>> query(UserGroupQuery query);

    ResultBean<List<AuthVO>> listAuth(BaseQuery query);

    ResultBean<List<UserGroupVO.RelaEmployeeVO>> listEmployee(BaseQuery query);

    ResultBean<Void> deleteRelaAuths(Long id, String authIds);

    ResultBean<Void> deleteRelaEmployees(Long id, String employeeIds);
}