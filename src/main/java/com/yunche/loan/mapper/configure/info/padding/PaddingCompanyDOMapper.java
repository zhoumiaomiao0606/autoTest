package com.yunche.loan.mapper.configure.info.padding;

import com.yunche.loan.obj.configure.info.padding.PaddingCompanyDO;
import com.yunche.loan.query.configure.info.address.BaseAreaQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaddingCompanyDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaddingCompanyDO record);

    int insertSelective(PaddingCompanyDO record);

    PaddingCompanyDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaddingCompanyDO record);

    int updateByPrimaryKeyWithBLOBs(PaddingCompanyDO record);

    int updateByPrimaryKey(PaddingCompanyDO record);

    int count(BaseAreaQuery query);

    List<PaddingCompanyDO> query(BaseAreaQuery query);
}