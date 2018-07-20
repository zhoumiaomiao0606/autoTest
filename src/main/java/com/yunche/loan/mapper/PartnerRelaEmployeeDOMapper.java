package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.entity.PartnerRelaEmployeeDO;
import com.yunche.loan.domain.entity.PartnerRelaEmployeeDOKey;

import java.util.List;

public interface PartnerRelaEmployeeDOMapper {

    int deleteByPrimaryKey(PartnerRelaEmployeeDOKey key);

    int insert(PartnerRelaEmployeeDO record);

    int insertSelective(PartnerRelaEmployeeDO record);

    PartnerRelaEmployeeDO selectByPrimaryKey(PartnerRelaEmployeeDOKey key);

    int updateByPrimaryKeySelective(PartnerRelaEmployeeDO record);

    int updateByPrimaryKey(PartnerRelaEmployeeDO record);

    int batchInsert(List<PartnerRelaEmployeeDO> partnerRelaEmployeeDOS);

    List<Long> getEmployeeIdListByPartnerId(Long partnerId);

    /**
     * 根据业务员ID获取所属合伙人ID
     *
     * @param employeeId
     * @return
     */
    Long getPartnerIdByEmployeeId(Long employeeId);

    /**
     * 根据业务员ID获取所属合伙人详情
     *
     * @param employeeId
     * @return
     */
    PartnerDO getPartnerByEmployeeId(Long employeeId);
}