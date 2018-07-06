package com.yunche.loan.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.IConstant;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.client.TestFeign;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/icbc")
public class ICBCController {

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

    @Autowired
    private TestFeign testFeign;
    //请求接口
    @PostMapping (value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> query(@RequestBody @Valid @Validated @NotNull ICBCApiCallbackParam.ApplyCreditCallback callback) {
        //return icbcFeignClient.applyCredit(applyCredit);
        bankSolutionService.creditAutomaticCommit(new Long("1806291133480804371"));
        return ResultBean.ofSuccess(null);
    }

    //请求接口
    @PostMapping (value = "/term", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> term(@RequestBody @Valid @Validated @NotNull  ICBCApiCallbackParam.ApplyDiviGeneralCallback callback) {
        //return icbcFeignClient.applyCredit(applyCredit);
        bankSolutionService.commonBusinessApply(new Long("1805241619246179093"));
        return ResultBean.ofSuccess(null);
    }

    //请求接口
    @PostMapping (value = "/test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> test() {
        //return icbcFeignClient.applyCredit(applyCredit);
        bankSolutionService.creditAutomaticCommit(new Long("1806291133480804371"));
        return ResultBean.ofSuccess(null);
    }

    //回调接口
    @PostMapping (value = "/creditresult", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void creditresult(@RequestParam String reqparam) throws IOException {
        reqparam = URLDecoder.decode(reqparam,"UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        testFeign.query(objectMapper.readValue(reqparam,ICBCApiCallbackParam.ApplyCreditCallback.class));
    }

    @PostMapping (value = "/creditreturn", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void creditreturn(@RequestParam String reqparam) throws IOException {
        reqparam = URLDecoder.decode(reqparam,"UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        testFeign.term(objectMapper.readValue(reqparam,ICBCApiCallbackParam.ApplyDiviGeneralCallback.class));
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


}
