package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.PartnerRelaEmployeeDO;
import com.yunche.loan.domain.entity.PartnerRelaEmployeeDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerRelaEmployeeDOMapper {
    int deleteByPrimaryKey(PartnerRelaEmployeeDOKey key);

    int insert(PartnerRelaEmployeeDO record);

    int insertSelective(PartnerRelaEmployeeDO record);

    PartnerRelaEmployeeDO selectByPrimaryKey(PartnerRelaEmployeeDOKey key);

    int updateByPrimaryKeySelective(PartnerRelaEmployeeDO record);

    int updateByPrimaryKey(PartnerRelaEmployeeDO record);

    List<Long> getEmployeeIdListByPartnerId(Long partnerId);

    int batchInsert(List<PartnerRelaEmployeeDO> partnerRelaEmployeeDOS);

    /**
     * 根据业务员ID获取所属合伙人ID
     *
     * @param employeeId
     * @return
     */
    Long getPartnerIdByEmployeeId(Long employeeId);
}