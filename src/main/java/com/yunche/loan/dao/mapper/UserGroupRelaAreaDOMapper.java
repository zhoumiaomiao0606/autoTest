package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.UserGroupRelaAreaDO;
import com.yunche.loan.domain.dataObj.UserGroupRelaAreaDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/26
 */
@Mapper
public interface UserGroupRelaAreaDOMapper {
    List<Long> getAreaIdListByUserGroupId(Long id);

    List<UserGroupRelaAreaDO> getByUserGroupId(Long userGroupId);

    int insert(UserGroupRelaAreaDO userGroupRelaAreaDO);

    int updateByUserGroupId(UserGroupRelaAreaDO userGroupRelaAreaDO);

    int deleteByPrimaryKey(UserGroupRelaAreaDOKey userGroupRelaAreaDOKey);
}
