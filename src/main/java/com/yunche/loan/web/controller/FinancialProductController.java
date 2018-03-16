package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FinancialProductParam;
import com.yunche.loan.domain.query.FinancialQuery;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.FinancialProductVO;
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
@RequestMapping("/financialProduct")
public class FinancialProductController {

    private static final Logger logger = LoggerFactory.getLogger(FinancialProductController.class);

    @Autowired
    private FinancialProductService financialProductService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Long> create(@RequestBody FinancialProductParam FinancialProductParam) {
        return financialProductService.insert(FinancialProductParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody FinancialProductParam financialProductParam) {
        logger.info(Arrays.asList("update", JSON.toJSONString(financialProductParam)).stream().collect(Collectors.joining("-")));
        return financialProductService.update(financialProductParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long prodId) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(prodId)).stream().collect(Collectors.joining("-")));
        return financialProductService.delete(prodId);
    }

    @GetMapping(value = "/disable")
    public ResultBean<Void> disable(@RequestParam("id") Long prodId) {
        logger.info(Arrays.asList("disable", JSON.toJSONString(prodId)).stream().collect(Collectors.joining("-")));
        return financialProductService.disable(prodId);
    }

    @GetMapping(value = "/enable")
    public ResultBean<Void> enable(@RequestParam("id") Long prodId) {
        logger.info(Arrays.asList("enable", JSON.toJSONString(prodId)).stream().collect(Collectors.joining("-")));
        return financialProductService.enable(prodId);
    }

    @GetMapping("/getById")
    public ResultBean<FinancialProductVO> getById(@RequestParam("id") Long prodId) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(prodId)).stream().collect(Collectors.joining("-")));
        return financialProductService.getById(prodId);
    }

    @PostMapping(value = "/getByCondition", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<FinancialProductVO>> query(@RequestBody FinancialQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return financialProductService.getByCondition(query);
    }

    /**
     * 通过合伙人ID 获取合伙人被授权的金融产品列表
     *
     * @param partnerId
     * @return
     */
    @GetMapping("/listByPartnerId")
    public ResultBean<List<BaseVO>> listByPartnerId(@RequestParam("partnerId") Long partnerId) {
        return financialProductService.listByPartnerId(partnerId);
    }
}


