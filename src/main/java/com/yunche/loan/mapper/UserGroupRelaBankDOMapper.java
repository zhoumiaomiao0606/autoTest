package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.UserGroupRelaBankDO;
import com.yunche.loan.domain.entity.UserGroupRelaBankDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserGroupRelaBankDOMapper {
    int deleteByPrimaryKey(UserGroupRelaBankDOKey key);

    int insert(UserGroupRelaBankDO record);

    int insertSelective(UserGroupRelaBankDO record);

    UserGroupRelaBankDO selectByPrimaryKey(UserGroupRelaBankDOKey key);

    int updateByPrimaryKeySelective(UserGroupRelaBankDO record);

    int updateByPrimaryKey(UserGroupRelaBankDO record);

    List<Long> getBankIdListByUserGroupId(Long userGroupId);
}