package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ThirdPartyFundBusinessDO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ThirdPartyFundBusinessDOMapper {

    int deleteByPrimaryKey(Long bridgeProcecssId);

    int insert(ThirdPartyFundBusinessDO record);

    int insertSelective(ThirdPartyFundBusinessDO record);

    ThirdPartyFundBusinessDO selectByPrimaryKey(Long bridgeProcecssId);

    int updateByPrimaryKeySelective(ThirdPartyFundBusinessDO record);

    int updateByPrimaryKey(ThirdPartyFundBusinessDO record);

    int batchInsert(List<ThirdPartyFundBusinessDO> thirdPartyFundBusinessDOS);

    int updateInfo(@Param("orderId")Long orderId,
                   @Param("repayDate")Date repayDate,
                   @Param("interest")BigDecimal interest,
                   @Param("poundage")BigDecimal poundage);

}