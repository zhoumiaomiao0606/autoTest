package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.entity.EmployeeRelaUserGroupDO;
import com.yunche.loan.domain.entity.EmployeeRelaUserGroupDOKey;

import java.util.List;

public interface EmployeeRelaUserGroupDOMapper {

    int deleteByPrimaryKey(EmployeeRelaUserGroupDOKey key);

    int insert(EmployeeRelaUserGroupDO record);

    int insertSelective(EmployeeRelaUserGroupDO record);

    EmployeeRelaUserGroupDO selectByPrimaryKey(EmployeeRelaUserGroupDOKey key);

    int updateByPrimaryKeySelective(EmployeeRelaUserGroupDO record);

    int updateByPrimaryKey(EmployeeRelaUserGroupDO record);

    List<Long> getEmployeeIdListByUserGroupId(Long userGroupId);

    /**
     * 获取用户组ID列表
     *
     * @param employeeId
     * @return
     */
    List<Long> getUserGroupIdListByEmployeeId(Long employeeId);

    int batchInsert(List<EmployeeRelaUserGroupDO> employeeRelaUserGroupDOS);

    int count(RelaQuery query);

    List<EmployeeRelaUserGroupDO> query(RelaQuery query);
}