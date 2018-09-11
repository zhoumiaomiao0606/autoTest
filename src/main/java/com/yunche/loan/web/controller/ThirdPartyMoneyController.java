package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfThirdPartyMoneyDO;
import com.yunche.loan.service.ThirdPartyMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/9/7
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/thirdPartyMoney")
public class ThirdPartyMoneyController {

    @Autowired
    private ThirdPartyMoneyService thirdPartyMoneyService;


    @GetMapping(value = "/create")
    public ResultBean<Long> create(@RequestBody ConfThirdPartyMoneyDO confThirdPartyMoneyDO) {
//        return ResultBean.ofSuccess(thirdPartyMoneyService.detail(confThirdPartyMoneyDO));
        return ResultBean.ofSuccess(null);
    }
}
