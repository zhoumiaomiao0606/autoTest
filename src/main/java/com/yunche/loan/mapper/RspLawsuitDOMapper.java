package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RspLawsuitDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface RspLawsuitDOMapper {
    int insert(RspLawsuitDO record);

    int insertSelective(RspLawsuitDO record);

    List<RspLawsuitDO> selectById(@Param("id")Long id);
}