package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.EmployeeQuery;
import com.yunche.loan.domain.QueryObj.RelaQuery;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EmployeeDO record);

    int insertSelective(EmployeeDO record);

    EmployeeDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(EmployeeDO record);

    int updateByPrimaryKey(EmployeeDO record);

    int count(EmployeeQuery query);

    List<EmployeeDO> query(EmployeeQuery query);

    List<EmployeeDO> getAll(@Param("status") Byte status);

    int countListEmployeeByUserGroupId(BaseQuery query);

    List<EmployeeDO> listEmployeeByUserGroupId(BaseQuery query);
}