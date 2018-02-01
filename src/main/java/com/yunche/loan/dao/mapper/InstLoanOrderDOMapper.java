package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InstLoanOrderDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(InstLoanOrderDO record);

    int insertSelective(InstLoanOrderDO record);

    InstLoanOrderDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(InstLoanOrderDO record);

    int updateByPrimaryKey(InstLoanOrderDO record);
}