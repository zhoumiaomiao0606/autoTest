package com.yunche.loan.config.feign.client;


import com.yunche.loan.config.feign.config.FeignConfig;
import com.yunche.loan.domain.param.ICBCApiCallbackParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@FeignClient(name = "testFeign" ,url = "http://wwzkdci.hk1.mofasuidao.cn/")
public interface TestFeign {
    @PostMapping(value = "/api/v1/loanorder/icbc/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String query(@Validated @NotNull @RequestBody ICBCApiCallbackParam.Callback callback);



    @PostMapping(value = "/api/v1/loanorder/icbc/term", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String term(@Validated @NotNull @RequestBody ICBCApiCallbackParam.Callback callback);
}
