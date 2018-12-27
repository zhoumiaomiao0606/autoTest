package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganXianchuDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganXianchuDOMapper {
    int insert(ZhonganXianchuDO record);

    int insertSelective(ZhonganXianchuDO record);

    List<ZhonganXianchuDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}