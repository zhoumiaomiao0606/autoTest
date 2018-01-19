package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.EmployeeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EmployeeDO record);

    int insertSelective(EmployeeDO record);

    EmployeeDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(EmployeeDO record);

    int updateByPrimaryKey(EmployeeDO record);
}