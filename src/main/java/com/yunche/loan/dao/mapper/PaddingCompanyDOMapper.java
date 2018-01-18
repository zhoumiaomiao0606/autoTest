package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.PaddingCompanyDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaddingCompanyDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PaddingCompanyDO record);

    int insertSelective(PaddingCompanyDO record);

    PaddingCompanyDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaddingCompanyDO record);

    int updateByPrimaryKeyWithBLOBs(PaddingCompanyDO record);

    int updateByPrimaryKey(PaddingCompanyDO record);

    int count(BaseAreaQuery query);

    List<PaddingCompanyDO> query(BaseAreaQuery query);
}