package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BizModelQuery;
import com.yunche.loan.domain.vo.BizModelRegionVO;
import com.yunche.loan.domain.vo.BizModelVO;
import com.yunche.loan.service.BizModelRelaAreaPartnersService;
import com.yunche.loan.service.BizModelRelaFinancialProdService;
import com.yunche.loan.service.BizModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/22.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/bizModel")
public class BizModelController {

    @Autowired
    private BizModelService bizModelService;


    @Autowired
    private BizModelRelaFinancialProdService bizModelRelaFinancialProdService;

    @Autowired
    private BizModelRelaAreaPartnersService bizModelRelaAreaPartnersService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> create(@RequestBody BizModelVO bizModelVO) {
        return bizModelService.insert(bizModelVO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody BizModelVO bizModelVO) {
        return bizModelService.update(bizModelVO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long bizId) {
        return bizModelService.delete(bizId);
    }

    @GetMapping(value = "/disable")
    public ResultBean<Void> disable(@RequestParam("id") Long bizId) {
        return bizModelService.disable(bizId);
    }

    @GetMapping(value = "/enable")
    public ResultBean<Void> enable(@RequestParam("id") Long bizId) {
        return bizModelService.enable(bizId);
    }

    @GetMapping("/getById")
    public ResultBean<BizModelVO> getById(@RequestParam("id") Long bizId) {
        return bizModelService.getById(bizId);
    }

    @PostMapping(value = "/getByCondition", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<BizModelVO>> query(@RequestBody BizModelQuery query) {
        return bizModelService.getByCondition(query);
    }

    @GetMapping(value = "/deleteRelaFinancialProd")
    public ResultBean<Void> deleteRelaFinancialProd(@RequestParam("bizId") Long bizId, @RequestParam("prodId") Long prodId) {
        return bizModelRelaFinancialProdService.deleteRelaFinancialProd(bizId, prodId);
    }

    @GetMapping(value = "/addRelaFinancialProd")
    public ResultBean<Void> addRelaFinancialProd(@RequestParam("bizId") Long bizId, @RequestParam("prodId") Long prodId) {
        return bizModelRelaFinancialProdService.addRelaFinancialProd(bizId, prodId);
    }

    @GetMapping(value = "/deleteRelaPartner")
    public ResultBean<Void> deleteRelaPartner(@RequestParam("bizId") Long bizId, @RequestParam("areaId") Long areaId, @RequestParam("groupId") Long groupId) {
        return bizModelRelaAreaPartnersService.deleteRelaPartner(bizId, areaId, groupId);
    }

    @PostMapping(value = "/addRelaPartner", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> addRelaPartner(@RequestBody BizModelRegionVO bizModelRegionVO) {
        return bizModelRelaAreaPartnersService.addRelaPartner(bizModelRegionVO);
    }
}


