package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.result.ResultBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "tenantFeignClient", url = "http://192.168.2.127:8016/")
public interface TenantFeignClient {


    @RequestMapping( value = "/app/api/tenant/{id}",method = RequestMethod.GET)
    public ResultBean queryDistributor(@PathVariable("id") String id);
}
