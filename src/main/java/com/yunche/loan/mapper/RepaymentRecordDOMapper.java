package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RepaymentRecordDO;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepaymentRecordDOMapper {
    int deleteByPrimaryKey(RepaymentRecordDOKey key);

    int insert(RepaymentRecordDO record);

    int insertSelective(RepaymentRecordDO record);

    RepaymentRecordDO selectByPrimaryKey(RepaymentRecordDOKey key);

    int updateByPrimaryKeySelective(RepaymentRecordDO record);

    int updateByPrimaryKey(RepaymentRecordDO record);
}