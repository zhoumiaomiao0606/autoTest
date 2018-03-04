package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.query.UserGroupQuery;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.vo.AuthVO;
import com.yunche.loan.domain.vo.EmployeeVO;
import com.yunche.loan.domain.vo.UserGroupVO;

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

    ResultBean<List<UserGroupVO>> batchGetById(List<Long> idList);

    ResultBean<List<UserGroupVO>> query(UserGroupQuery query);

    ResultBean<List<AuthVO>> listAuth(BaseQuery query);

    ResultBean<List<EmployeeVO>> listBindEmployee(EmployeeQuery query);

    ResultBean<List<EmployeeVO>> listUnbindEmployee(EmployeeQuery query);

    ResultBean<Void> bindEmployee(Long id, String employeeIds);

    ResultBean<Void> unbindEmployee(Long id, String employeeIds);

    ResultBean<Void> editAuth(Long id, Long areaId, String authIds, Byte type);

    ResultBean<Void> bindAuth(Long id, Long areaId, String authIds, Byte type);

    ResultBean<Void> unbindAuth(Long id, String authIds, Byte type);
}
