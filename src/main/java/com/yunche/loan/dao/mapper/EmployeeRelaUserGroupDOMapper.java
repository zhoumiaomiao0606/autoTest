package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.RelaQuery;
import com.yunche.loan.domain.dataObj.EmployeeRelaUserGroupDO;
import com.yunche.loan.domain.dataObj.EmployeeRelaUserGroupDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeRelaUserGroupDOMapper {
    int deleteByPrimaryKey(EmployeeRelaUserGroupDOKey key);

    int insert(EmployeeRelaUserGroupDO record);

    int insertSelective(EmployeeRelaUserGroupDO record);

    EmployeeRelaUserGroupDO selectByPrimaryKey(EmployeeRelaUserGroupDOKey key);

    int updateByPrimaryKeySelective(EmployeeRelaUserGroupDO record);

    int updateByPrimaryKey(EmployeeRelaUserGroupDO record);

    List<Long> getEmployeeIdListByUserGroupId(Long userGroupId);

    List<Long> getUserGroupIdListByEmployeeId(Long employeeId);

    int batchInsert(List<EmployeeRelaUserGroupDO> employeeRelaUserGroupDOS);

    int count(RelaQuery query);

    List<EmployeeRelaUserGroupDO> query(RelaQuery query);
}