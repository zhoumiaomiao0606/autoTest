package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.CustRelaPersonInfoDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustRelaPersonInfoDOMapper {
    int deleteByPrimaryKey(Long custId);

    int insert(CustRelaPersonInfoDO record);

    int insertSelective(CustRelaPersonInfoDO record);

    CustRelaPersonInfoDO selectByPrimaryKey(Long custId);

    int updateByPrimaryKeySelective(CustRelaPersonInfoDO record);

    int updateByPrimaryKey(CustRelaPersonInfoDO record);
}