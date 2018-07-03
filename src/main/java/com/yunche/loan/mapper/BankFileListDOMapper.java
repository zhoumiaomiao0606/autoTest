package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankFileListDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankFileListDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BankFileListDO record);

    int insertSelective(BankFileListDO record);

    BankFileListDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankFileListDO record);

    int updateByPrimaryKey(BankFileListDO record);
}