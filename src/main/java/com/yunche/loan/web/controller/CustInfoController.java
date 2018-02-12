package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.CustRelaPersonInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.CustService;
import com.yunche.loan.service.LoanOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/12.
 */
@CrossOrigin
@RestController
@RequestMapping("/custInfo")
public class CustInfoController {

    private static final Logger logger = LoggerFactory.getLogger(CustInfoController.class);

    @Autowired
    private CustService custService;

    /**
     * 创建主贷人信息
     * @param custBaseInfoVO
     * @return
     */
    @PostMapping(value = "/createMain", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Long> createMain(@RequestBody CustBaseInfoVO custBaseInfoVO) {
        return custService.createMainCust(custBaseInfoVO);
    }


    /**
     * 更新主贷人信息
     * @param custBaseInfoVO
     * @return
     */
    @PostMapping(value = "/updateMain", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Long> updateMain(@RequestBody CustBaseInfoVO custBaseInfoVO) {
        return custService.updateMainCust(custBaseInfoVO);
    }

    /**
     * 创建关联人信息
     * @param custRelaPersonInfoVO
     * @return
     */
    @PostMapping(value = "/createRela", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Long> createMain(@RequestBody CustRelaPersonInfoVO custRelaPersonInfoVO) {
        return custService.createRelaCust(custRelaPersonInfoVO);
    }


    /**
     * 更新关联人信息
     * @param custRelaPersonInfoVO
     * @return
     */
    @PostMapping(value = "/updateRela", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Long> updateMain(@RequestBody CustRelaPersonInfoVO custRelaPersonInfoVO) {
        return custService.updateRelaCust(custRelaPersonInfoVO);
    }
}


