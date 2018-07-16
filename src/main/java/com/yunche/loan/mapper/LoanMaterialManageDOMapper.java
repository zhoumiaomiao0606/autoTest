package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanMaterialManageDO;

public interface LoanMaterialManageDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(LoanMaterialManageDO record);

    int insertSelective(LoanMaterialManageDO record);

    LoanMaterialManageDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanMaterialManageDO record);

    int updateByPrimaryKey(LoanMaterialManageDO record);
}