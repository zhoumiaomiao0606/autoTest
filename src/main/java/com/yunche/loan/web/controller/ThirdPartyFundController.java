package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ThirdPartyFundDO;
import com.yunche.loan.service.ThirdPartyFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-05 09:39
 * @description: 第三方资金
 **/
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/thirdPartyFund")
public class ThirdPartyFundController
{
    @Autowired
    private ThirdPartyFundService thirdPartyFundService;

    @GetMapping(value = "/list")
    public ResultBean list()
    {
        return ResultBean.ofSuccess(thirdPartyFundService.list());
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:
    */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam Long third_party_fund_id){
        return ResultBean.ofSuccess(thirdPartyFundService.detail(third_party_fund_id));
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:修改和停用
    */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody ThirdPartyFundDO param) {
        thirdPartyFundService.update(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }
}
