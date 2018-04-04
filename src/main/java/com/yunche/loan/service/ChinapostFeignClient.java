package com.yunche.loan.service;


import com.yunche.loan.config.feign.ChinapostFeignClientConfig;
import com.yunche.loan.domain.vo.Postcode;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "chinapostFeignClient",url = "http://cpdc.chinapost.com.cn/",configuration = ChinapostFeignClientConfig.class)
public interface ChinapostFeignClient {

    @RequestMapping(value = "/web/index.php",method = RequestMethod.GET)
    public Postcode getPostcodeData(@RequestParam("searchkey") String searchkey);

}
