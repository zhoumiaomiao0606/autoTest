package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.QueryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.service.BizModelService;
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
 * Created by zhouguoliang on 2018/1/22.
 */
@CrossOrigin
@RestController
@RequestMapping("/bizModel")
public class BizModelController {

    private static final Logger logger = LoggerFactory.getLogger(BizModelController.class);

    @Autowired
    private BizModelService bizModelService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> create(@RequestBody BizModelVO bizModelVO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(bizModelVO)).stream().collect(Collectors.joining("-")));
        return bizModelService.insert(bizModelVO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody BizModelVO bizModelVO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(bizModelVO)).stream().collect(Collectors.joining("-")));
        return bizModelService.update(bizModelVO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long prodId) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(prodId)).stream().collect(Collectors.joining("-")));
        return bizModelService.delete(prodId);
    }

    @GetMapping("/getById")
    public ResultBean<BizModelVO> getById(@RequestParam("id") Long bizId) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(bizId)).stream().collect(Collectors.joining("-")));
        return bizModelService.getById(bizId);
    }

    @PostMapping(value = "/getByCondition", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<BizModelVO>> query(@RequestBody BizModelQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return bizModelService.getByCondition(query);
    }
}


