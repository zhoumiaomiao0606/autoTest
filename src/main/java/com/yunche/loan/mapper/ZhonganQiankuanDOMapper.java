package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganQiankuanDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganQiankuanDOMapper {
    int insert(ZhonganQiankuanDO record);

    int insertSelective(ZhonganQiankuanDO record);

    List<ZhonganQiankuanDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}