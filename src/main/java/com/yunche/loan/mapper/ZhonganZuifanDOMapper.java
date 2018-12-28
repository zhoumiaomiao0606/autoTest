package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganZuifanDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganZuifanDOMapper {
    int insert(ZhonganZuifanDO record);

    int insertSelective(ZhonganZuifanDO record);

    List<ZhonganZuifanDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}