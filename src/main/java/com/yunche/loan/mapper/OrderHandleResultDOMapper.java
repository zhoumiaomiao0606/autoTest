package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.OrderHandleResultDO;

public interface OrderHandleResultDOMapper {
    int deleteByPrimaryKey(Long orderid);

    int insert(OrderHandleResultDO record);

    int insertSelective(OrderHandleResultDO record);

    OrderHandleResultDO selectByPrimaryKey(Long orderid);

    int updateByPrimaryKeySelective(OrderHandleResultDO record);

    int updateByPrimaryKey(OrderHandleResultDO record);
}