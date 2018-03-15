package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCarInfoDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanCarInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCarInfoDO record);

    int insertSelective(LoanCarInfoDO record);

    LoanCarInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanCarInfoDO record);

    int updateByPrimaryKey(LoanCarInfoDO record);
}