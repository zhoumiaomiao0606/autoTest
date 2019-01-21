package com.yunche.loan.service;

import com.github.pagehelper.PageInfo;
import com.yunche.loan.domain.query.OrderListQuery;
import com.yunche.loan.domain.vo.OrderListVO;

/**
 * @author liuzhe
 * @date 2019/1/9
 */
public interface OrderListService {

    PageInfo<OrderListVO> materialReview(OrderListQuery query);
}
