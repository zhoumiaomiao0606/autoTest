package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganShenpanDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganShenpanDOMapper {
    int insert(ZhonganShenpanDO record);

    int insertSelective(ZhonganShenpanDO record);

    List<ZhonganShenpanDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}