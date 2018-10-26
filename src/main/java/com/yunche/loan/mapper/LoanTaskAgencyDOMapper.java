package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanTaskAgencyDO;
import com.yunche.loan.domain.entity.LoanTaskAgencyDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanTaskAgencyDOMapper {
    int deleteByPrimaryKey(LoanTaskAgencyDOKey key);

    int insert(LoanTaskAgencyDO record);

    int insertSelective(LoanTaskAgencyDO record);

    LoanTaskAgencyDO selectByPrimaryKey(LoanTaskAgencyDOKey key);

    int updateByPrimaryKeySelective(LoanTaskAgencyDO record);

    int updateByPrimaryKey(LoanTaskAgencyDO record);
}