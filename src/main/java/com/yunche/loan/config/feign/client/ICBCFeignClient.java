package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.feign.config.FeignConfig;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.response.ApplyCreditResponse;
import com.yunche.loan.config.feign.response.ApplyDiviGeneralResponse;
import com.yunche.loan.config.feign.response.CreditCardApplyResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "icbcFeignClient" ,url = "http://122.225.203.102:18090/",configuration = FeignConfig.class)
//@FeignClient(name = "iCBCFeignClient" ,url = "http://localhost:18090/")
public interface ICBCFeignClient {

    //wanggang
    //征信查询
    @RequestMapping(value = "/api/v1/icbc/apply/applyCredit",method = RequestMethod.POST)
    public ApplyCreditResponse applyCredit(@RequestBody ICBCApiRequest.ApplyCredit applyCredit);

    //通用申请接口
    @RequestMapping(value = "/api/v1/icbc/apply/applydivigeneral",method = RequestMethod.POST)
    public ApplyDiviGeneralResponse applyDiviGeneral(@RequestBody ICBCApiRequest.ApplyDiviGeneral applyDiviGeneral);

    //通用资料补偿申请接口
    @RequestMapping(value = "/api/v1/icbc/apply/multimediaUpload",method = RequestMethod.POST)
    public ApplyDiviGeneralResponse multimediaUpload(@RequestBody ICBCApiRequest.MultimediaUpload multimediaUpload);

    //zhengdu
    @RequestMapping(value = "/api/v1/icbc/test/apply/creditcardapply",method = RequestMethod.POST)
    public CreditCardApplyResponse creditcardapply(@RequestBody ICBCApiRequest.ApplyBankOpenCard applyBankOpenCard);

    @RequestMapping(value = "/api/v1/test/icbc/apply/filedownload",method = RequestMethod.GET)
    public boolean filedownload(@RequestParam(value = "filesrc") String  filesrc);


}
