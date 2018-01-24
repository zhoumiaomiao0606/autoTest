package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.UserGroupQuery;
import com.yunche.loan.domain.dataObj.UserGroupDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserGroupDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserGroupDO record);

    int insertSelective(UserGroupDO record);

    UserGroupDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(UserGroupDO record);

    int updateByPrimaryKeyWithBLOBs(UserGroupDO record);

    int updateByPrimaryKey(UserGroupDO record);

    List<String> getAllName(@Param("status") Byte status);

    int count(UserGroupQuery query);

    List<UserGroupDO> query(UserGroupQuery query);
}