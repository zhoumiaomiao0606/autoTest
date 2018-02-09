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
     * @param type        权限类型  1:MENU; 2:PAGE; 3:OPERATION;
     * @return
     */
    List<Long> getAllHasAuthByUserGroupId(@Param("userGroupId") Long userGroupId, @Param("type") Byte type);


    /**
     * 根据权限类型 将权限实体ID 映射到 AuthID
     *
     * @param authEntityIdList
     * @param type             权限类型  1:MENU; 2:PAGE; 3:OPERATION;
     * @return
     */
    List<Long> convertToAuthId(@Param("authEntityIdList") List<Long> authEntityIdList, @Param("type") Byte type);

    /**
     * 获取所有Auth
     *
     * @return
     */
    List<AuthDO> getAll();
}