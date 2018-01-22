package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.BizModelRelaAreaDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizModelRelaAreaDOMapper {
    int deleteByPrimaryKey(BizModelRelaAreaDO key);

    int insert(BizModelRelaAreaDO record);

    int insertSelective(BizModelRelaAreaDO record);
}