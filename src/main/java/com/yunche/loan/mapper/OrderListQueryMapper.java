package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.OrderListQuery;
import com.yunche.loan.domain.vo.OrderListVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/1/9
 */
public interface OrderListQueryMapper {

    List<OrderListVO> materialReview(OrderListQuery query);
}
