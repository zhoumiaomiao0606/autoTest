package com.yunche.loan.web.controller;

import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.OrderListQuery;
import com.yunche.loan.domain.vo.OrderListVO;
import com.yunche.loan.service.OrderListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/1/9
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/orderList")
public class OrderListController {

    @Autowired
    private OrderListService orderListService;


    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<OrderListVO>> query(@RequestBody OrderListQuery query) {
        PageInfo<OrderListVO> pageInfo = orderListService.query(query);
        return ResultBean.ofPageInfo(pageInfo);
    }
}
