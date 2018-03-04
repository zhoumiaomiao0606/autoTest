package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.LoanBaseInfoDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanBaseInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanBaseInfoDO record);

    int insertSelective(LoanBaseInfoDO record);

    LoanBaseInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanBaseInfoDO record);

    int updateByPrimaryKey(LoanBaseInfoDO record);
}