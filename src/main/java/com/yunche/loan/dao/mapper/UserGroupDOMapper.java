package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.queryObj.BaseQuery;
import com.yunche.loan.domain.queryObj.UserGroupQuery;
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

    /**
     * 根据员工ID统计关联的用户组总量
     *
     * @param query
     * @return
     */
    int countListUserGroupByEmployeeId(BaseQuery query);

    /**
     * 根据员工ID分页查询关联的用户组
     *
     * @param query
     * @return
     */
    List<UserGroupDO> listUserGroupByEmployeeId(BaseQuery query);

    /**
     * 根据部门ID统计关联的用户组总量
     *
     * @param query
     * @return
     */
    int countListUserGroupByDepartmentId(BaseQuery query);

    /**
     * 根据部门ID分页查询关联的用户组
     *
     * @param query
     * @return
     */
    List<UserGroupDO> listUserGroupByDepartmentId(BaseQuery query);

}