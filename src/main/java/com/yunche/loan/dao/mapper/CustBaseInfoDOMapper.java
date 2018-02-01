package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.CustBaseInfoDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustBaseInfoDOMapper {
    int deleteByPrimaryKey(Long custId);

    int insert(CustBaseInfoDO record);

    int insertSelective(CustBaseInfoDO record);

    CustBaseInfoDO selectByPrimaryKey(Long custId);

    int updateByPrimaryKeySelective(CustBaseInfoDO record);

    int updateByPrimaryKey(CustBaseInfoDO record);
}