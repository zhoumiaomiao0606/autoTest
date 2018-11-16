package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VoucherErrRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VoucherErrRecordDOMapper {
    int deleteByPrimaryKey(String serialNo);

    int insert(VoucherErrRecordDO record);

    int insertSelective(VoucherErrRecordDO record);

    VoucherErrRecordDO selectByPrimaryKey(String serialNo);

    int updateByPrimaryKeySelective(VoucherErrRecordDO record);

    int updateByPrimaryKey(VoucherErrRecordDO record);
}