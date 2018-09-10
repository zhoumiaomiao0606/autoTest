package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ThirdPartyFundBusinessDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ThirdPartyFundBusinessDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(ThirdPartyFundBusinessDO record);

    int insertSelective(ThirdPartyFundBusinessDO record);

    ThirdPartyFundBusinessDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(ThirdPartyFundBusinessDO record);

    int updateByPrimaryKey(ThirdPartyFundBusinessDO record);

    int batchInsert(List<ThirdPartyFundBusinessDO> thirdPartyFundBusinessDOS);
}