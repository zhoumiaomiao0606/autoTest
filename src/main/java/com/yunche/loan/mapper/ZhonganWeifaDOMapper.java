package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganWeifaDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganWeifaDOMapper {
    int insert(ZhonganWeifaDO record);

    int insertSelective(ZhonganWeifaDO record);

    List<ZhonganWeifaDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}