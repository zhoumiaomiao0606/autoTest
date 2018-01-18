package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import com.yunche.loan.domain.QueryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.valueObj.BaseAreaVO;
import com.yunche.loan.service.CarService;
import com.yunche.loan.service.FinancialProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@CrossOrigin
@RestController
@RequestMapping("/config/financialproduct")
public class FinancialProductController {

    private static final Logger logger = LoggerFactory.getLogger(FinancialProductController.class);

    @Autowired
    private FinancialProductService financialProductService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> create(@RequestBody FinancialProductDO financialProductDO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(financialProductDO)).stream().collect(Collectors.joining("-")));
        return financialProductService.insert(financialProductDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody FinancialProductDO financialProductDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(financialProductDO)).stream().collect(Collectors.joining("-")));
        return financialProductService.update(financialProductDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long prodId) {
        logger.info(Arrays.asList("delete", prodId.toString()).stream().collect(Collectors.joining("-")));
        return financialProductService.delete(prodId);
    }

    @GetMapping("/getById")
    public ResultBean<FinancialProductDO> getById(@RequestParam("id") Long prodId) {
        logger.info(Arrays.asList("getById", prodId.toString()).stream().collect(Collectors.joining("-")));
        return financialProductService.getById(prodId);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<FinancialProductDO>> query(@RequestBody FinancialQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return financialProductService.getByCondition(query);
    }
}


