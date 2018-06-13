package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ICBCApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "icbcFeignClient" ,url = "http://122.225.203.102:8090/")
public interface ICBCService {
    @RequestMapping(value = "/api/v1/icbc/apply/applyCredit",method = RequestMethod.POST)
    public ResultBean applyCredit(@RequestBody ICBCApiParam.ApplyCredit applyCredit);
}
