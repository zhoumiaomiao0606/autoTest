package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.queryObj.BaseQuery;
import com.yunche.loan.domain.queryObj.EmployeeQuery;
import com.yunche.loan.domain.queryObj.RelaQuery;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import com.yunche.loan.domain.viewObj.EmployeeOnlyPropertyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmployeeDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(EmployeeDO record);

    int insertSelective(EmployeeDO record);

    EmployeeDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(EmployeeDO record);

    int updateByPrimaryKey(EmployeeDO record);

    int count(EmployeeQuery query);

    List<EmployeeDO> query(EmployeeQuery query);

    List<EmployeeDO> getAll(@Param("status") Byte status);

    List<String> listTitle();

    int countListEmployeeByUserGroupId(BaseQuery query);

    List<EmployeeDO> listEmployeeByUserGroupId(BaseQuery query);

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
}