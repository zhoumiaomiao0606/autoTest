package com.yunche.loan.dao;

import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.UserGroupQuery;
import com.yunche.loan.domain.entity.UserGroupDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserGroupDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserGroupDO record);

    int insertSelective(UserGroupDO record);

    UserGroupDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    List<UserGroupDO> batchSelectByPrimaryKey(@Param("idList") List<Long> idList, @Param("status") Byte status);

    int updateByPrimaryKeySelective(UserGroupDO record);

    int updateByPrimaryKeyWithBLOBs(UserGroupDO record);

    int updateByPrimaryKey(UserGroupDO record);

    List<String> getAllName(@Param("status") Byte status);

    int count(UserGroupQuery query);

    List<UserGroupDO> query(UserGroupQuery query);

    /**
     * 获取用户组基本信息列表  【id + name】
     *
     * @param employeeId
     * @return
     */
    List<UserGroupDO> getBaseUserGroupByEmployeeId(Long employeeId);

    /**
     * 根据员工ID和区域 统计关联的用户组总量
     *
     * @param query
     * @return
     */
    int countListUserGroupByEmployeeIdAndAreaList(BaseQuery query);

    /**
     * 根据员工ID和区域 分页查询关联的用户组
     *
     * @param query
     * @return
     */
    List<UserGroupDO> listUserGroupByEmployeeIdAndAreaList(BaseQuery query);

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