package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganQianshuiDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganQianshuiDOMapper {
    int insert(ZhonganQianshuiDO record);

    int insertSelective(ZhonganQianshuiDO record);

    List<ZhonganQianshuiDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}