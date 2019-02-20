package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankOnlineTransDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BankOnlineTransDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(BankOnlineTransDO record);

    int insertSelective(BankOnlineTransDO record);

    BankOnlineTransDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(BankOnlineTransDO record);

    int updateByPrimaryKey(BankOnlineTransDO record);
}