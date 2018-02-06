package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.BaseQuery;
import com.yunche.loan.domain.queryObj.EmployeeQuery;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
public interface EmployeeService {

    ResultBean<Long> create(EmployeeParam employeeParam) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    ResultBean<Void> update(EmployeeDO employeeDO);

    ResultBean<Void> delete(Long id);

    ResultBean<EmployeeVO> getById(Long id);

    ResultBean<List<EmployeeVO>> query(EmployeeQuery query);

    ResultBean<List<LevelVO>> listAll();

    ResultBean<List<String>> listTitle();

    ResultBean<List<UserGroupVO>> listUserGroup(BaseQuery query);

    ResultBean<Void> bindUserGroup(Long id, String userGroupIds);

    ResultBean<Void> unbindUserGroup(Long id, String userGroupIds);

    ResultBean<Void> resetPassword(Long id);
}
