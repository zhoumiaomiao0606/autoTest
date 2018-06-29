package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.param.ICBCApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.constraints.NotNull;

//@FeignClient(name = "icbcFeignClient" ,url = "http://122.225.203.102:8090/")
@FeignClient(name = "iCBCFeignClient" ,url = "http://localhost:18090/")
public interface ICBCFeignClient {
    @RequestMapping(value = "/api/v1/icbc/apply/applyCredit",method = RequestMethod.POST)
    public ResultBean applyCredit(@RequestBody @Validated @NotNull ICBCApiParam.ApplyCredit applyCredit);


    @RequestMapping(value = "/api/v1/icbc/test/apply/creditcardapply",method = RequestMethod.POST)
    public ResultBean creditcardapply(@RequestBody BankOpenCardParam bankOpenCardParam);

}
