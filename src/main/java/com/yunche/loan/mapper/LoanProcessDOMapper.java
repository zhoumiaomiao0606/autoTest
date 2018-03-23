package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanProcessDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanProcessDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(LoanProcessDO record);

    int insertSelective(LoanProcessDO record);

    LoanProcessDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(LoanProcessDO record);

    int updateByPrimaryKey(LoanProcessDO record);
}