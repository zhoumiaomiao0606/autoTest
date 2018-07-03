package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankFileListRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface BankFileListRecordDOMapper {
    int deleteByPrimaryKey(Long bankFileListId);

    int insert(BankFileListRecordDO record);

    int insertSelective(BankFileListRecordDO record);

    BankFileListRecordDO selectByPrimaryKey(Long bankFileListId);

    int updateByPrimaryKeySelective(BankFileListRecordDO record);

    int updateByPrimaryKey(BankFileListRecordDO record);

    int insertBatch(List<BankFileListRecordDO> recordLists);
}