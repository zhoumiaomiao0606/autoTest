package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.InsuranceCompanyDO;
import com.yunche.loan.domain.QueryObj.InsuranceCompanyQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InsuranceCompanyDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InsuranceCompanyDO record);

    int insertSelective(InsuranceCompanyDO record);

    InsuranceCompanyDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsuranceCompanyDO record);

    int updateByPrimaryKeyWithBLOBs(InsuranceCompanyDO record);

    int updateByPrimaryKey(InsuranceCompanyDO record);

    int count(InsuranceCompanyQuery query);

    List<InsuranceCompanyDO> query(InsuranceCompanyQuery query);
}