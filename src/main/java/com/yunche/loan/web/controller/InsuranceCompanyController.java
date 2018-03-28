package com.yunche.loan.web.controller;

import com.yunche.loan.domain.vo.InsuranceCompanyVO;
import com.yunche.loan.domain.entity.InsuranceCompanyDO;
import com.yunche.loan.domain.query.InsuranceCompanyQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.InsuranceCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/insurance")
public class InsuranceCompanyController {

    @Autowired
    private InsuranceCompanyService insuranceCompanyService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody InsuranceCompanyDO insuranceCompanyDO) {
        return insuranceCompanyService.create(insuranceCompanyDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody InsuranceCompanyDO insuranceCompanyDO) {
        return insuranceCompanyService.update(insuranceCompanyDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return insuranceCompanyService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<InsuranceCompanyVO> getById(@RequestParam("id") Long id) {
        return insuranceCompanyService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<InsuranceCompanyVO>> query(@RequestBody InsuranceCompanyQuery query) {
        return insuranceCompanyService.query(query);
    }
}
