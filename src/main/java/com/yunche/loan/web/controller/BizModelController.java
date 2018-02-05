package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.BizModelQuery;
import com.yunche.loan.domain.viewObj.BizModelRegionVO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.service.BizModelRelaAreaPartnersService;
import com.yunche.loan.service.BizModelRelaFinancialProdService;
import com.yunche.loan.service.BizModelService;
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

    @Autowired
    private BizModelRelaFinancialProdService bizModelRelaFinancialProdService;

    @Autowired
    private BizModelRelaAreaPartnersService bizModelRelaAreaPartnersService;

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
    public ResultBean<Void> delete(@RequestParam("id") Long bizId) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(bizId)).stream().collect(Collectors.joining("-")));
        return bizModelService.delete(bizId);
    }

    @GetMapping(value = "/disable")
    public ResultBean<Void> disable(@RequestParam("id") Long bizId) {
        logger.info(Arrays.asList("disable", JSON.toJSONString(bizId)).stream().collect(Collectors.joining("-")));
        return bizModelService.disable(bizId);
    }

    @GetMapping(value = "/enable")
    public ResultBean<Void> enable(@RequestParam("id") Long bizId) {
        logger.info(Arrays.asList("enable", JSON.toJSONString(bizId)).stream().collect(Collectors.joining("-")));
        return bizModelService.enable(bizId);
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

    @GetMapping(value = "/deleteRelaFinancialProd")
    public ResultBean<Void> deleteRelaFinancialProd(@RequestParam("bizId") Long bizId, @RequestParam("prodId") Long prodId) {
        logger.info(Arrays.asList("deleteRelaFinancialProd", JSON.toJSONString(bizId)).stream().collect(Collectors.joining("-")));
        return bizModelRelaFinancialProdService.deleteRelaFinancialProd(bizId, prodId);
    }

    @GetMapping(value = "/addRelaFinancialProd")
    public ResultBean<Void> addRelaFinancialProd(@RequestParam("bizId") Long bizId, @RequestParam("prodId") Long prodId) {
        logger.info(Arrays.asList("addRelaFinancialProd", JSON.toJSONString(bizId)).stream().collect(Collectors.joining("-")));
        return bizModelRelaFinancialProdService.addRelaFinancialProd(bizId, prodId);
    }

    @GetMapping(value = "/deleteRelaPartner")
    public ResultBean<Void> deleteRelaPartner(@RequestParam("bizId") Long bizId, @RequestParam("areaId") Long areaId, @RequestParam("groupId") Long groupId) {
        logger.info(Arrays.asList("deleteRelaPartner", JSON.toJSONString(bizId)).stream().collect(Collectors.joining("-")));
        return bizModelRelaAreaPartnersService.deleteRelaPartner(bizId, areaId, groupId);
    }

    @PostMapping(value = "/addRelaPartner")
    public ResultBean<Void> addRelaPartner(@RequestBody BizModelRegionVO bizModelRegionVO) {
        logger.info(Arrays.asList("create", JSON.toJSONString(bizModelRegionVO)).stream().collect(Collectors.joining("-")));
        return bizModelRelaAreaPartnersService.addRelaPartner(bizModelRegionVO);
    }
}


