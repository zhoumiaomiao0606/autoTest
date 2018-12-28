package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganZhixingDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganZhixingDOMapper {
    int insert(ZhonganZhixingDO record);

    int insertSelective(ZhonganZhixingDO record);

    List<ZhonganZhixingDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}