package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.queryObj.PaddingCompanyQuery;
import com.yunche.loan.domain.dataObj.PaddingCompanyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaddingCompanyDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PaddingCompanyDO record);

    int insertSelective(PaddingCompanyDO record);

    PaddingCompanyDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(PaddingCompanyDO record);

    int updateByPrimaryKeyWithBLOBs(PaddingCompanyDO record);

    int updateByPrimaryKey(PaddingCompanyDO record);

    int count(PaddingCompanyQuery query);

    List<PaddingCompanyDO> query(PaddingCompanyQuery query);
}