package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.entity.EmployeeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeDOMapper {

    int setMachineIdForNull(Long id);

    int deleteByPrimaryKey(Long id);

    int insert(EmployeeDO record);

    int insertSelective(EmployeeDO record);

    EmployeeDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(EmployeeDO record);

    int updateByPrimaryKey(EmployeeDO record);

    int count(EmployeeQuery query);

    List<EmployeeDO> query(EmployeeQuery query);

    List<EmployeeDO> getAll(@Param("type") Byte type, @Param("status") Byte status);

    List<String> listTitle();

    /**
     * 根据合伙人 统计关联的(外包)员工总数
     *
     * @param query
     * @return
     */
    int countListEmployeeByPartnerId(RelaQuery query);

    /**
     * 根据合伙人 分页查询关联的(外包)员工详情
     *
     * @param query
     * @return
     */
    List<EmployeeDO> listEmployeeByPartnerId(RelaQuery query);

    /**
     * 根据ID列表获取
     *
     * @param idList
     * @return
     */
    List<EmployeeDO> getByIdList(List<Long> idList);

    /**
     * 获取所有员工ID
     *
     * @param status
     * @return
     */
    List<Long> getAllEmployeeIdList(@Param("status") Byte status);

    /**
     * 条件查询 - 获取所有员工ID
     *
     * @param query
     * @return
     */
    List<Long> queryAllEmployeeIdList(EmployeeQuery query);

    /**
     * 获取所有的唯一属性列表 （身份证号、手机号、邮箱、钉钉）
     *
     * @return
     */
    List<EmployeeDO> getAllOnlyProperty();

    /**
     * 根据唯一账号(邮箱📮) 获取用户
     *
     * @param email
     * @return
     */
    EmployeeDO getByUsername(@Param("email") String email, @Param("status") Byte status);

    /**
     * 通过ID获取名称
     *
     * @param visitSalesmanId
     * @return
     */
    String getNameById(Long visitSalesmanId);

    /**
     * 将parentId置空
     *
     * @param id
     * @return
     */
    void setParentIdIsNull(Long id);

    /**
     * 替换parentId
     *
     * @param oldParentId
     * @param newParentId
     */
    int replaceParentId(@Param("oldParentId") Long oldParentId, @Param("newParentId") Long newParentId);

    /**
     * 更新parentId
     *
     * @param id
     * @param parentId
     */
    int updateParentIdById(@Param("id") Long id, @Param("parentId") Long parentId);

    /**
     * 获取所有子账号
     *
     * @param parentId
     * @return
     */
    List<Long> listChildByParentId(Long parentId);
}