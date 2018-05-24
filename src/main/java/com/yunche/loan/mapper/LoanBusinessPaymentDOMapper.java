package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanBusinessPaymentDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanBusinessPaymentDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(LoanBusinessPaymentDO record);

    int insertSelective(LoanBusinessPaymentDO record);

    LoanBusinessPaymentDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanBusinessPaymentDO record);

    int updateByPrimaryKey(LoanBusinessPaymentDO record);
}