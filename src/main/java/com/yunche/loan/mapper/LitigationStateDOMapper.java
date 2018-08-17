package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LitigationStateDO;
import org.apache.ibatis.annotations.Param;

public interface LitigationStateDOMapper {
    int insert(LitigationStateDO record);

    int insertSelective(LitigationStateDO record);

    LitigationStateDO selectByIdAndType(@Param("id")Long id,@Param("type")String type);
}