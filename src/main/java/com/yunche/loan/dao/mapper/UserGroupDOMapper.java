package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.UserGroupDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserGroupDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserGroupDO record);

    int insertSelective(UserGroupDO record);

    UserGroupDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserGroupDO record);

    int updateByPrimaryKeyWithBLOBs(UserGroupDO record);

    int updateByPrimaryKey(UserGroupDO record);
}