package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.response.ApplyStatusResponse;
import com.yunche.loan.config.feign.response.ApplycreditstatusResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "iCBCFeignNormal" ,url = "http://localhost:18090/")
public interface ICBCFeignNormal {


    @RequestMapping(value = "/api/v1/test/icbc/apply/filedownload",method = RequestMethod.GET)
    public boolean filedownload(@RequestParam(value = "filesrc") String filesrc);



}
