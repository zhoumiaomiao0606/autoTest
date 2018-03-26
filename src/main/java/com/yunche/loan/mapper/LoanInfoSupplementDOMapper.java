package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanInfoSupplementDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanInfoSupplementDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanInfoSupplementDO record);

    int insertSelective(LoanInfoSupplementDO record);

    LoanInfoSupplementDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanInfoSupplementDO record);

    int updateByPrimaryKey(LoanInfoSupplementDO record);

    int countByOrderId(Long orderId);
}