package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RspCreditDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RspCreditDOMapper {
    int insert(RspCreditDO record);

    int insertSelective(RspCreditDO record);

    List<RspCreditDO> selectById(@Param("id")Long id);
}