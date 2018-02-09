package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.LoanOrderService;
import com.yunche.loan.service.LoanProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/8.
 */
@CrossOrigin
@RestController
@RequestMapping("/loanOrder")
public class LoanOrderController {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderController.class);

    @Autowired
    private LoanOrderService loanOrderService;

    /**
     * 查询各个流程环节的订单列表
     * @param orderListQuery
     * @return
     */
    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<InstLoanOrderVO>> list(@RequestBody OrderListQuery orderListQuery) {
        return loanOrderService.queryOrderList(orderListQuery);
    }


    /**
     * 业务订单详情
     * @param orderId
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean<InstLoanOrderVO> detail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.detail(orderId);
    }
}


