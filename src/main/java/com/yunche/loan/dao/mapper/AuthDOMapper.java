package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.AuthDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AuthDO record);

    int insertSelective(AuthDO record);

    AuthDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AuthDO record);

    int updateByPrimaryKey(AuthDO record);

    /**
     * 根据权限类型，查询当前用户组已授权的权限ID列表
     *
     * @param userGroupId
     * @param type        权限类型
     * @return
     */
    List<Long> getAllHasAuthByUserGroupId(@Param("userGroupId") Long userGroupId, @Param("type") Byte type);
}