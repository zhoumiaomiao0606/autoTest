package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.DepartmentQuery;
import com.yunche.loan.domain.entity.DepartmentDO;
import com.yunche.loan.domain.param.DepartmentParam;
import com.yunche.loan.domain.vo.DepartmentVO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.UserGroupVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
public interface DepartmentService {
    ResultBean<Long> create(DepartmentParam departmentParam);

    ResultBean<Void> update(DepartmentDO departmentDO);

    ResultBean<Void> delete(Long id);

    ResultBean<DepartmentVO> getById(Long id);

    ResultBean<List<DepartmentVO>> query(DepartmentQuery query);

    ResultBean<List<CascadeVO>> listAll();

    ResultBean<Void> bindUserGroup(Long id, String userGroupIds);

    ResultBean<Void> unbindUserGroup(Long id, String userGroupIds);

    ResultBean<List<UserGroupVO>> listUserGroup(BaseQuery query);
}
