package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.feign.config.FeignConfig;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "icbcFeignClient", url = "http://122.225.203.102:18090/", configuration = FeignConfig.class)
//@FeignClient(name = "icbcFeignClient", url = "http://122.225.203.102:9030/", configuration = FeignConfig.class)
//@FeignClient(name = "iCBCFeignClient" ,url = "http://192.168.0.168:18090/",configuration = FeignConfig.class)
public interface ICBCFeignClient {

    //wanggang
    //征信查询
    @RequestMapping(value = "/api/v1/icbc/apply/applyCredit", method = RequestMethod.POST)
    ApplyCreditResponse applyCredit(@RequestBody ICBCApiRequest.ApplyCredit applyCredit);

    //通用申请接口
    @RequestMapping(value = "/api/v1/icbc/apply/applydivigeneral", method = RequestMethod.POST)
    ApplyDiviGeneralResponse applyDiviGeneral(@RequestBody ICBCApiRequest.ApplyDiviGeneral applyDiviGeneral);

    //通用资料补偿申请接口
    @RequestMapping(value = "/api/v1/icbc/apply/multimediaupload", method = RequestMethod.POST)
    MultimediaUploadResponse multimediaUpload(@RequestBody ICBCApiRequest.MultimediaUpload multimediaUpload);

    //zhengdu
    @RequestMapping(value = "/api/v1/icbc/apply/creditcardapply", method = RequestMethod.POST)
    CreditCardApplyResponse creditcardapply(@RequestBody ICBCApiRequest.ApplyBankOpenCard applyBankOpenCard);


    //查询申请进度
    @RequestMapping(value = "/api/v1/icbc/apply/applystatus", method = RequestMethod.POST)
    ApplyStatusResponse applyStatus(@RequestBody ICBCApiRequest.Applystatus applystatus);


    //查询专项卡开卡进度
    @RequestMapping(value = "/api/v1/icbc/apply/applycreditstatus", method = RequestMethod.POST)
    ApplycreditstatusResponse applycreditstatus(@RequestBody ICBCApiRequest.Applycreditstatus applycreditstatus);


}
