package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganXiangaoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganXiangaoDOMapper {
    int insert(ZhonganXiangaoDO record);

    int insertSelective(ZhonganXiangaoDO record);

    List<ZhonganXiangaoDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}