package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfThirdPartyMoneyDO;
import com.yunche.loan.domain.query.ConfThirdPartyMoneyQuery;
import com.yunche.loan.service.ConfThirdPartyMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 三方资金基础配置
 *
 * @author liuzhe
 * @date 2018/9/5
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/confThirdPartyMoney")
public class ConfThirdPartyMoneyController {

    @Autowired
    private ConfThirdPartyMoneyService confThirdPartyMoneyService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody ConfThirdPartyMoneyDO confThirdPartyMoneyDO) {
        return ResultBean.ofSuccess(confThirdPartyMoneyService.create(confThirdPartyMoneyDO));
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody ConfThirdPartyMoneyDO confThirdPartyMoneyDO) {
        return ResultBean.ofSuccess(confThirdPartyMoneyService.update(confThirdPartyMoneyDO));
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return ResultBean.ofSuccess(confThirdPartyMoneyService.delete(id));
    }

    @GetMapping("/detail")
    public ResultBean<ConfThirdPartyMoneyDO> detail(@RequestParam("id") Long id) {
        return ResultBean.ofSuccess(confThirdPartyMoneyService.detail(id));
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<ConfThirdPartyMoneyDO>> query(@RequestBody ConfThirdPartyMoneyQuery query) {
        return confThirdPartyMoneyService.query(query);
    }
}
