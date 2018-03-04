package com.yunche.loan.dao;

import com.yunche.loan.domain.query.DepartmentQuery;
import com.yunche.loan.domain.entity.DepartmentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DepartmentDO record);

    int insertSelective(DepartmentDO record);

    DepartmentDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(DepartmentDO record);

    int updateByPrimaryKey(DepartmentDO record);

    int count(DepartmentQuery query);

    List<DepartmentDO> query(DepartmentQuery query);

    List<DepartmentDO> getAll(@Param("status") Byte status);

    List<String> getAllName(@Param("status") Byte status);

    List<DepartmentDO> getByParentId(@Param("parentId") Long parentId, @Param("status") Byte status);
}