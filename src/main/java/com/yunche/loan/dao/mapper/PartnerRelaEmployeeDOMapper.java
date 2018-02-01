package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.PartnerRelaEmployeeDO;
import com.yunche.loan.domain.dataObj.PartnerRelaEmployeeDOKey;
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
}