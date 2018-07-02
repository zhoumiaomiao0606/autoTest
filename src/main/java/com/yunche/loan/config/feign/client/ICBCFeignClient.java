package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.feign.config.FeignConfig;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.response.ApplyCreditResponse;
import com.yunche.loan.config.feign.response.CreditCardApplyResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@FeignClient(name = "icbcFeignClient" ,url = "http://122.225.203.102:18090/",configuration = FeignConfig.class)
//@FeignClient(name = "iCBCFeignClient" ,url = "http://192.168.0.166:18090",configuration = FeignLogConfig.class)
public interface ICBCFeignClient {

    @RequestMapping(value = "/api/v1/test/icbc/apply/applyCredit",method = RequestMethod.POST)
    public ApplyCreditResponse applyCredit(@RequestBody @Validated @NotNull ICBCApiRequest.ApplyCredit applyCredit);


    @RequestMapping(value = "/api/v1/icbc/test/apply/creditcardapply",method = RequestMethod.POST)
    public CreditCardApplyResponse creditcardapply(@RequestBody ICBCApiRequest.ApplyBankOpenCard applyBankOpenCard);

    @GetMapping(value = "/api/v1/icbc/test/apply/filedownload")
    public boolean filedownload(@RequestParam("filesrc") String  filesrc);
}
