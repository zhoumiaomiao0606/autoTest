package com.yunche.loan.config.feign.client;

import com.yunche.loan.domain.param.DistributorParam;
import com.yunche.loan.domain.vo.DistributorVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@FeignClient(name = "tenantFeignClient", url = "http://47.110.7.61:8016")
@FeignClient(name = "tenantFeignClient", url = "http://47.96.78.20:8016")
public interface TenantFeignClient {


    @RequestMapping( value = "/app/api/tenant/{id}",method = RequestMethod.GET)
    DistributorVO queryDistributor(@PathVariable(value = "id") String id);

    @RequestMapping( value = "/app/api/order",method = RequestMethod.POST)
    DistributorVO saveOrder(@RequestBody DistributorParam param);

    @RequestMapping( value = "/app/api/order/{orderId}",method = RequestMethod.PUT)
    DistributorVO modifyOrder(@PathVariable(value = "orderId") String orderId,@RequestBody DistributorParam param);



}
