package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LegworkReimbursementFileDO;

import java.util.List;

public interface LegworkReimbursementFileDOMapper {
    int deleteByPrimaryKey(Long id);

    void deleteByLegworkReimbursementId(Long legworkReimbursementId);

    int insertSelective(LegworkReimbursementFileDO record);

    LegworkReimbursementFileDO selectByPrimaryKey(Long id);

    List<LegworkReimbursementFileDO> selectByLegworkReimbursementId(Long legworkReimbursementId);

    int updateByPrimaryKeySelective(LegworkReimbursementFileDO record);
}