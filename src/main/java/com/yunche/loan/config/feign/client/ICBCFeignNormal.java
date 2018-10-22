package com.yunche.loan.config.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "iCBCFeignNormal", url = "http://122.225.203.102:18090/")
//@FeignClient(name = "iCBCFeignNormal", url = "http://122.225.203.102:9030/")
public interface ICBCFeignNormal {


    @RequestMapping(value = "/api/v1/icbc/apply/filedownload", method = RequestMethod.GET)
    boolean filedownload(@RequestParam(value = "filesrc") String filesrc,
                         @RequestParam(value = "fileType") String fileType);

}
