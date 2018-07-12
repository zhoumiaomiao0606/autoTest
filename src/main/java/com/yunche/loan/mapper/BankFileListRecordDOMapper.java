package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankFileListRecordDO;
import com.yunche.loan.domain.entity.BankFileListRecordDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface BankFileListRecordDOMapper {

    BankFileListRecordDO selectNewestByOrderId(Long orderId);

    int deleteByPrimaryKey(BankFileListRecordDOKey key);

    int insert(BankFileListRecordDO record);

    int insertSelective(BankFileListRecordDO record);

    BankFileListRecordDO selectByPrimaryKey(BankFileListRecordDOKey key);

    int updateByPrimaryKeySelective(BankFileListRecordDO record);

    int updateByPrimaryKey(BankFileListRecordDO record);

    int insertBatch(List<BankFileListRecordDO> recordLists);

    int deleteBylistId(Long listId);
}