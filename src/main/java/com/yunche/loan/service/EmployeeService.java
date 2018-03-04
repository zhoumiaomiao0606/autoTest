package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.vo.EmployeeVO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.LoginVO;
import com.yunche.loan.domain.vo.UserGroupVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
public interface EmployeeService {

    ResultBean<Long> create(EmployeeParam employeeParam);

    ResultBean<Void> update(EmployeeDO employeeDO);

    ResultBean<Void> delete(Long id);

    ResultBean<EmployeeVO> getById(Long id);

    ResultBean<List<EmployeeVO>> query(EmployeeQuery query);

    ResultBean<List<CascadeVO>> listAll();

    ResultBean<List<String>> listTitle();

    ResultBean<List<UserGroupVO>> listUserGroup(RelaQuery query);

    ResultBean<Void> bindUserGroup(Long id, String userGroupIds);

    ResultBean<Void> unbindUserGroup(Long id, String userGroupIds);

    ResultBean<Void> resetPassword(Long id);

    ResultBean<LoginVO> login(HttpServletRequest request, HttpServletResponse response, EmployeeParam employeeParam);

    ResultBean<Void> logout();

    ResultBean<Void> editPassword(EmployeeParam employeeParam);
}
