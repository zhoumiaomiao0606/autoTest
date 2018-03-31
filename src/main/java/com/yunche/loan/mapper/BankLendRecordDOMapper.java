package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankLendRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankLendRecordDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankLendRecordDO record);

    int insertSelective(BankLendRecordDO record);

    BankLendRecordDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankLendRecordDO record);

    int updateByPrimaryKey(BankLendRecordDO record);

    BankLendRecordDO selectByLoanOrder(Long loanOrder);
}