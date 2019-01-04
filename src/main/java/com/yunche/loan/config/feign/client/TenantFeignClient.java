package com.yunche.loan.config.feign.client;

import com.yunche.loan.domain.vo.DistributorVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "tenantFeignClient", url = "http://192.168.2.118:8016")
public interface TenantFeignClient {


    @RequestMapping( value = "/app/api/tenant/{id}",method = RequestMethod.GET)
    DistributorVO queryDistributor(@PathVariable(value = "id") String id);
}
