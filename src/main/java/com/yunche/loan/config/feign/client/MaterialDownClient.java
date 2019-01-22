package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.result.ResultBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "materialDownClient", url = "http://172.16.169.66:7002/")
//@FeignClient(name = "materialDownClient", url = "http://192.168.2.182:8001/")
public interface MaterialDownClient {

    @RequestMapping(value = "/api/v1/loanorder/material/down2oss", method = RequestMethod.GET)
    ResultBean down2OSS(@RequestParam(value = "orderId") Long orderId,@RequestParam(value = "reGenerateZip") Boolean reGenerateZip);

    @RequestMapping(value = "/api/v1/loanorder/material/downsup2oss", method = RequestMethod.GET)
    ResultBean downSup2OSS(@RequestParam(value = "orderId") Long orderId,@RequestParam(value = "infoSupplementId")Long infoSupplementId);
}