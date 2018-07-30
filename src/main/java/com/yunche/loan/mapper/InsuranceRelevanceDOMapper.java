package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InsuranceRelevanceDOMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByInsuranceInfoId(Long insuranceInfoId);

    int insertSelective(InsuranceRelevanceDO record);

    InsuranceRelevanceDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsuranceRelevanceDO record);

    /**
     * 获取关联保险列表
     *
     * @param insuranceInfoId
     * @return
     */
    List<InsuranceRelevanceDO> listByInsuranceInfoId(Long insuranceInfoId);

    int deleteByInsuranceInfoIdAndType(Long insuranceInfoId,Byte type);
}