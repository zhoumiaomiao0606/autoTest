package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VoucherRecordDO;
import com.yunche.loan.domain.entity.VoucherRecordDOKey;

public interface VoucherRecordDOMapper {
    int deleteByPrimaryKey(VoucherRecordDOKey key);

    int insert(VoucherRecordDO record);

    int insertSelective(VoucherRecordDO record);

    VoucherRecordDO selectByPrimaryKey(VoucherRecordDOKey key);

    int updateByPrimaryKeySelective(VoucherRecordDO record);

    int updateByPrimaryKey(VoucherRecordDO record);
}