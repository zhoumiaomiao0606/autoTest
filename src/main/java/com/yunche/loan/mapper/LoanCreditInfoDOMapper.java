package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanCreditInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCreditInfoDO record);

    int insertSelective(LoanCreditInfoDO record);

    LoanCreditInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCreditInfoDO record);

    int updateByPrimaryKey(LoanCreditInfoDO record);

    List<LoanCreditInfoDO> getByCustomerIdAndType(@Param("customerId") Long customerId, @Param("type") Byte type);

    String bankNoByCusId(@Param("customerId") Long customerId);
}