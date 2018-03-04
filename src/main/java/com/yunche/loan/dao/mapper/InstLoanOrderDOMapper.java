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

    InstLoanOrderDO selectByProcInstId(String procInstId);

    int updateByPrimaryKeySelective(InstLoanOrderDO record);

    int updateByPrimaryKey(InstLoanOrderDO record);

    int count(OrderListQuery query);

    List<InstLoanOrderDO> queryByCondition(OrderListQuery query);

    /**
     * 获取主贷人ID
     *
     * @param orderId
     * @return
     */
    Long getCustIdById(Long orderId);
}