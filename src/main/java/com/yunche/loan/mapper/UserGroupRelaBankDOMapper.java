package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.UserGroupRelaBankDO;
import com.yunche.loan.domain.entity.UserGroupRelaBankDOKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserGroupRelaBankDOMapper {

    int deleteByPrimaryKey(UserGroupRelaBankDOKey key);

    int insert(UserGroupRelaBankDO record);

    int insertSelective(UserGroupRelaBankDO record);

    UserGroupRelaBankDO selectByPrimaryKey(UserGroupRelaBankDOKey key);

    int updateByPrimaryKeySelective(UserGroupRelaBankDO record);

    int updateByPrimaryKey(UserGroupRelaBankDO record);

    List<String> getBankNameListByUserGroupId(Long userGroupId);

    int deleteAllByUserGroupId(Long userGroupId);

    List<Long> listBankIdByUserGroupIdList(@Param("userGroupIdList") List<Long> userGroupIdList);

    List<String> listBankNameByUserGroupIdList(@Param("userGroupIdList") List<Long> userGroupIdList);
}