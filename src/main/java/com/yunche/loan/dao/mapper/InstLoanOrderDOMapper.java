package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InstLoanOrderDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(InstLoanOrderDO record);

    int insertSelective(InstLoanOrderDO record);

    InstLoanOrderDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(InstLoanOrderDO record);

    int updateByPrimaryKey(InstLoanOrderDO record);

    List<InstLoanOrderDO> queryByCondition(OrderListQuery creditApplyQuery);
}