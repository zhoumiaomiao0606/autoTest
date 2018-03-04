package com.yunche.loan.dao;

import com.yunche.loan.domain.entity.EmployeeRelaDepartmentDO;
import com.yunche.loan.domain.entity.EmployeeRelaDepartmentDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeRelaDepartmentDOMapper {
    int deleteByPrimaryKey(EmployeeRelaDepartmentDOKey key);

    int insert(EmployeeRelaDepartmentDO record);

    int insertSelective(EmployeeRelaDepartmentDO record);

    EmployeeRelaDepartmentDO selectByPrimaryKey(EmployeeRelaDepartmentDOKey key);

    int updateByPrimaryKeySelective(EmployeeRelaDepartmentDO record);

    int updateByPrimaryKey(EmployeeRelaDepartmentDO record);

    List<Long> getDepartmentIdListByEmployeeId(Long employeeId);
}