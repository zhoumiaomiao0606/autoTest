package com.yunche.loan.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.IConstant;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.domain.entity.MaterialDownHisDO;
import com.yunche.loan.domain.param.ICBCApiCallbackParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.MaterialDownHisDOMapper;
import com.yunche.loan.service.BankSolutionProcessService;
import com.yunche.loan.service.BankSolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/icbc")
public class ICBCController {

    private static final String SUCCESS_RETCODR="00000";
    private static final String ERROR_RETCODR="20000";

    private static final String SUCCESS_RETMSG="成功";
    private static final String ERROR_RETMSG="程序出错";
    @Autowired
    private ICBCFeignClient icbcFeignClient;

    @Autowired
    private BankSolutionProcessService bankSolutionProcessService;

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Autowired
    private BankSolutionService bankSolutionService;

    @Autowired
    private MaterialDownHisDOMapper materialDownHisDOMapper;


    //回调接口
    @PostMapping (value = "/creditresult", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String creditresult(@RequestParam String reqparam) throws IOException {
        try {
            reqparam = URLDecoder.decode(reqparam,"UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            bankSolutionProcessService.applyCreditCallback(objectMapper.readValue(reqparam,ICBCApiCallbackParam.ApplyCreditCallback.class));
            return returnResponse(SUCCESS_RETCODR,SUCCESS_RETMSG);
        }catch (Exception e){
            return returnResponse(ERROR_RETCODR,ERROR_RETMSG);
        }
    }

    @PostMapping (value = "/creditreturn", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String creditreturn(@RequestParam String reqparam) throws IOException {
        try {
            reqparam = URLDecoder.decode(reqparam,"UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            bankSolutionProcessService.applyDiviGeneralCallback(objectMapper.readValue(reqparam,ICBCApiCallbackParam.ApplyDiviGeneralCallback.class));
            return returnResponse(SUCCESS_RETCODR,SUCCESS_RETMSG);
        }catch (Exception e){
            return returnResponse(ERROR_RETCODR,ERROR_RETMSG);
        }
    }

    @PostMapping (value = "/multimediaUploadreturn", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String multimediaUploadreturn(@RequestParam String reqparam) throws IOException {
        try {
            reqparam = URLDecoder.decode(reqparam,"UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            bankSolutionProcessService.multimediaUploadCallback(objectMapper.readValue(reqparam,ICBCApiCallbackParam.MultimediaUploadCallback.class));
            return returnResponse(SUCCESS_RETCODR,SUCCESS_RETMSG);
        }catch (Exception e){
            return returnResponse(ERROR_RETCODR,ERROR_RETMSG);
        }
    }


    @PostMapping (value = "/creditcardresult", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResultBean creditcardresult(@RequestParam String reqparam) {
        return null;
    }

    @PostMapping (value = "/fileNotice", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResultBean fileNotice( @RequestParam String reqparam) throws IOException {
        reqparam = URLDecoder.decode(reqparam,"UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        ICBCApiRequest.FileNotice fileNotice = objectMapper.readValue(reqparam,ICBCApiRequest.FileNotice.class);
        //记录回调信息
        MaterialDownHisDO materialDownHisDO = new MaterialDownHisDO();
        materialDownHisDO.setSerialNo(GeneratorIDUtil.execute());
        materialDownHisDO.setFileType(fileNotice.getReq().getFiletype());
        materialDownHisDO.setFileName(fileNotice.getReq().getFilesrc());
        materialDownHisDO.setStatus(IDict.K_JYZT.PRE_TRANSACTION);
        int count = materialDownHisDOMapper.insertSelective(materialDownHisDO);
        Preconditions.checkArgument(count>0,"插入文件清单流水异常");
        ICBCApiRequest.ReturnMsg returnMsg = new ICBCApiRequest.ReturnMsg();
        returnMsg.getPub().setRetcode(IConstant.SUCCESS);
        returnMsg.getPub().setRetmsg("成功");
        return ResultBean.ofSuccess(returnMsg);

    }


    public String returnResponse(String code,String msg){
        /*00000– 成功
        1****-参数上送错误(修改参数后可直接重复提交)
        2****-程序处理错误(含业务规则控制不符等)
        3****-系统错误(出现此错误先通知我们之后可以重新提交*/
        ICBCApiCallbackParam.Response response = new ICBCApiCallbackParam.Response();
        ICBCApiCallbackParam.ResponsePub responsePub = new ICBCApiCallbackParam.ResponsePub();
        responsePub.setRetcode(code);
        responsePub.setRetmsg(msg);
        response.setPub(responsePub);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String str = new String("{\"pub\": {\"retcode\": \""+ERROR_RETCODR+"\",\"retmsg\": \""+ERROR_RETMSG+"\"}}") ;
        return str;
    }


}
