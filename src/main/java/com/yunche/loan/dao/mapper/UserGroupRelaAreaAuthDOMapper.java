package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.UserGroupRelaAreaAuthDO;
import com.yunche.loan.domain.dataObj.UserGroupRelaAreaAuthDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserGroupRelaAreaAuthDOMapper {
    int deleteByPrimaryKey(UserGroupRelaAreaAuthDOKey key);

    int insert(UserGroupRelaAreaAuthDO record);

    int insertSelective(UserGroupRelaAreaAuthDO record);

    UserGroupRelaAreaAuthDO selectByPrimaryKey(UserGroupRelaAreaAuthDOKey key);

    int updateByPrimaryKeySelective(UserGroupRelaAreaAuthDO record);

    int updateByPrimaryKey(UserGroupRelaAreaAuthDO record);

    /**
     * 条件查询
     *
     * @param userGroupRelaAreaAuthDOKey
     * @return
     */
    List<UserGroupRelaAreaAuthDO> query(UserGroupRelaAreaAuthDOKey userGroupRelaAreaAuthDOKey);

    /**
     * 批量插入
     *
     * @param userGroupRelaAreaAuthDOS
     * @return
     */
    int batchInsert(List<UserGroupRelaAreaAuthDO> userGroupRelaAreaAuthDOS);

    /**
     * 通过userGroupId编辑可选字段
     *
     * @param userGroupRelaAreaAuthDO
     * @return
     */
    int updateByUserGroupIdSelective(UserGroupRelaAreaAuthDO userGroupRelaAreaAuthDO);

    List<Long> getAreaIdListByUserGroupId(Long userGroupId);
}