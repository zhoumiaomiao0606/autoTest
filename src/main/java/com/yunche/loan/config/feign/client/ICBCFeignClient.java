package com.yunche.loan.config.feign.client;

import com.yunche.loan.config.feign.config.FeignConfig;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.response.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    @RequestMapping(value = "/api/v1/icbc/apply/multimediaupload",method = RequestMethod.POST)
    public MultimediaUploadResponse multimediaUpload(@RequestBody ICBCApiRequest.MultimediaUpload multimediaUpload);

    //zhengdu
    @RequestMapping(value = "/api/v1/icbc/apply/creditcardapply",method = RequestMethod.POST)
    public CreditCardApplyResponse creditcardapply(@RequestBody ICBCApiRequest.ApplyBankOpenCard applyBankOpenCard);

    @RequestMapping(value = "/api/v1/test/icbc/apply/filedownload",method = RequestMethod.GET)
    public boolean filedownload(@RequestParam(value = "filesrc") String  filesrc,@RequestParam(value = "fileType") String  fileType);

    //查询申请进度
    @RequestMapping(value = "/api/v1/icbc/apply/applystatus",method = RequestMethod.POST)
    public ApplyStatusResponse applyStatus(@RequestBody ICBCApiRequest.Applystatus applystatus);


    //查询专项卡开卡进度
    @RequestMapping(value = "/api/v1/icbc/apply/applycreditstatus",method = RequestMethod.POST)
    public ApplycreditstatusResponse applycreditstatus(@RequestBody ICBCApiRequest.Applycreditstatus applycreditstatus);


}
