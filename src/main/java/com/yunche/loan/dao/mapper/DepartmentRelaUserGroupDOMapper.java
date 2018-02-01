package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.queryObj.BaseQuery;
import com.yunche.loan.domain.dataObj.DepartmentRelaUserGroupDO;
import com.yunche.loan.domain.dataObj.DepartmentRelaUserGroupDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentRelaUserGroupDOMapper {
    int deleteByPrimaryKey(DepartmentRelaUserGroupDOKey key);

    int insert(DepartmentRelaUserGroupDO record);

    int insertSelective(DepartmentRelaUserGroupDO record);

    DepartmentRelaUserGroupDO selectByPrimaryKey(DepartmentRelaUserGroupDOKey key);

    int updateByPrimaryKeySelective(DepartmentRelaUserGroupDO record);

    int updateByPrimaryKey(DepartmentRelaUserGroupDO record);

    int count(BaseQuery query);

    List<DepartmentRelaUserGroupDO> query(BaseQuery query);

    List<Long> getUserGroupIdListByDepartmentId(Long departmentId);

    int batchInsert(List<DepartmentRelaUserGroupDO> departmentRelaUserGroupDOS);

    List<Long> getDepartmentIdListByUserGroupId(Long id);

    List<DepartmentRelaUserGroupDO> getByUserGroupId(Long userGroupId);

    int updateByUserGroupId(DepartmentRelaUserGroupDO departmentRelaUserGroupDO);
}