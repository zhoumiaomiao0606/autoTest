package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfThirdRealBridgeProcessDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfThirdRealBridgeProcessDOMapper {
    int deleteByPrimaryKey(Long bridgeProcessId);

    int insert(ConfThirdRealBridgeProcessDO record);

    int insertSelective(ConfThirdRealBridgeProcessDO record);

    ConfThirdRealBridgeProcessDO selectByPrimaryKey(Long bridgeProcessId);

    int updateByPrimaryKeySelective(ConfThirdRealBridgeProcessDO record);

    int updateByPrimaryKey(ConfThirdRealBridgeProcessDO record);
}