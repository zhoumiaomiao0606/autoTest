package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.domain.query.OrderListQuery;
import com.yunche.loan.domain.vo.CreditApplyOrderListVO;
import com.yunche.loan.mapper.OrderListQueryMapper;
import com.yunche.loan.service.OrderListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/1/9
 */
@Service
public class OrderListServiceImpl implements OrderListService {

    @Autowired
    private OrderListQueryMapper orderListQueryMapper;


    @Override
    public PageInfo<CreditApplyOrderListVO> creditApply(OrderListQuery query) {

        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<CreditApplyOrderListVO> creditApplyOrderListVOList = orderListQueryMapper.creditApply(query);

        PageInfo<CreditApplyOrderListVO> pageInfo = PageInfo.of(creditApplyOrderListVOList);

        return pageInfo;
    }
}
