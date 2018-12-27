package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganCaipanDO;
import com.yunche.loan.domain.entity.ZhonganFeizhengDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhonganFeizhengDOMapper {
    int insert(ZhonganFeizhengDO record);

    int insertSelective(ZhonganFeizhengDO record);

    List<ZhonganFeizhengDO> selectByZhonganId(@Param("zhonganId") Long zhonganId);
}