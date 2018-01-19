package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.QueryObj.BizAreaQuery;
import com.yunche.loan.domain.dataObj.BizAreaDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizAreaDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BizAreaDO record);

    int insertSelective(BizAreaDO record);

    BizAreaDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BizAreaDO record);

    int updateByPrimaryKeyWithBLOBs(BizAreaDO record);

    int updateByPrimaryKey(BizAreaDO record);

    List<String> getAllName();

    int count(BizAreaQuery query);

    List<BizAreaDO> query(BizAreaQuery query);
}