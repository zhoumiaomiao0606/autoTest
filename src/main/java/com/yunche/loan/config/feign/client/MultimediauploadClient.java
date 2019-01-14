package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.MultimediaUploadParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "multimediauploadClient", url = "http://172.16.169.66:7002/")
public interface MultimediauploadClient {

    @RequestMapping( value = "/api/v1/loanorder/icbc/multimediaupload",method = RequestMethod.POST)
    ResultBean multimediaUpload(@RequestBody MultimediaUploadParam param);
}
