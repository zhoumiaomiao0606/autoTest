package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganShixinDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganShixinDOMapper {
    int insert(ZhonganShixinDO record);

    int insertSelective(ZhonganShixinDO record);

    List<ZhonganShixinDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}