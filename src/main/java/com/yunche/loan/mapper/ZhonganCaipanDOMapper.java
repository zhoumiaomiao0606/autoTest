package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganCaipanDOMapper {
    int insert(ZhonganCaipanDO record);

    int insertSelective(ZhonganCaipanDO record);

    List<ZhonganCaipanDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}