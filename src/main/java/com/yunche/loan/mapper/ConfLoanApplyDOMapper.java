package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfLoanApplyDO;
import com.yunche.loan.domain.entity.ConfLoanApplyDOKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConfLoanApplyDOMapper {
    int deleteByPrimaryKey(ConfLoanApplyDOKey key);

    int insert(ConfLoanApplyDO record);

    int insertSelective(ConfLoanApplyDO record);

    ConfLoanApplyDO selectByPrimaryKey(ConfLoanApplyDOKey key);

    int updateByPrimaryKeySelective(ConfLoanApplyDO record);

    int updateByPrimaryKey(ConfLoanApplyDO record);

    List<ConfLoanApplyDO> selectInfoByBank(@Param("bank")String bank);
}