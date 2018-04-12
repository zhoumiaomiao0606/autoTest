package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FinancialProductParam;
import com.yunche.loan.domain.query.FinancialQuery;
import com.yunche.loan.domain.vo.CascadeFinancialProductVO;
import com.yunche.loan.domain.vo.FinancialProductVO;
import com.yunche.loan.service.FinancialProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/financialProduct")
public class FinancialProductController {

    @Autowired
    private FinancialProductService financialProductService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Long> create(@RequestBody FinancialProductParam FinancialProductParam) {
        return financialProductService.insert(FinancialProductParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> update(@RequestBody FinancialProductParam financialProductParam) {
        return financialProductService.update(financialProductParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long prodId) {
        return financialProductService.delete(prodId);
    }

    @GetMapping(value = "/disable")
    public ResultBean<Void> disable(@RequestParam("id") Long prodId) {
        return financialProductService.disable(prodId);
    }

    @GetMapping(value = "/enable")
    public ResultBean<Void> enable(@RequestParam("id") Long prodId) {
        return financialProductService.enable(prodId);
    }

    @GetMapping("/getById")
    public ResultBean<FinancialProductVO> getById(@RequestParam("id") Long prodId) {
        return financialProductService.getById(prodId);
    }

    @PostMapping(value = "/getByCondition", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<List<FinancialProductVO>> query(@RequestBody FinancialQuery query) {
        return financialProductService.getByCondition(query);
    }

    /**
     * 通过orderId 获取合伙人被授权的金融产品列表
     *
     * @param orderId
     * @return
     */
    @GetMapping("/listByOrderId")
    public ResultBean<List<CascadeFinancialProductVO>> listByOrderId(@RequestParam("orderId") Long orderId) {
        return financialProductService.listByOrderId(orderId);
    }
}


