package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankRelaQuestionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BankRelaQuestionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BankRelaQuestionDO record);

    int insertSelective(BankRelaQuestionDO record);

    BankRelaQuestionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BankRelaQuestionDO record);

    int updateByPrimaryKey(BankRelaQuestionDO record);

    int deleteAllByBankId(Long bankId);

    /**
     * @param bankId
     * @param type
     * @return
     */
    List<BankRelaQuestionDO> listByBankIdAndType(Long bankId, Byte type);
}