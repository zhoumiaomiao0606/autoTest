package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankCodeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BankCodeDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankCodeDO record);

    int insertSelective(BankCodeDO record);

    BankCodeDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankCodeDO record);

    int updateByPrimaryKey(BankCodeDO record);

    List<BankCodeDO> selectByBankName(@Param("bankName") String bankName,@Param("level") Byte level);

    List<BankCodeDO> selectBankNameByParentId(Integer bankId);

    BankCodeDO selectByBankNameIsExist(String name);
}