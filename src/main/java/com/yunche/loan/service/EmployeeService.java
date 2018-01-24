package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.EmployeeQuery;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.LevelVO;

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

    ResultBean<List<LevelVO>> listAll();
}
