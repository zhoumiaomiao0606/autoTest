package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.IConstant;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.domain.entity.MaterialDownHisDO;
import com.yunche.loan.domain.param.ICBCApiCallbackParam;
import com.yunche.loan.domain.param.ICBCApiParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.MaterialDownHisDOMapper;
import com.yunche.loan.service.BankSolutionProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

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
    private MaterialDownHisDOMapper materialDownHisDOMapper;
    //请求接口
    @PostMapping (value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResultBean<Long> query( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        //return icbcFeignClient.applyCredit(applyCredit);
        return null;
    }




    //回调接口
    @PostMapping (value = "/creditresult", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditresult(@Validated @NotNull @RequestBody ICBCApiCallbackParam.Callback callback) {
        return null;
    }


    @PostMapping (value = "/creditreturn", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditreturn( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return null;
    }

    @PostMapping (value = "/creditcardresult", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean creditcardresult( @RequestBody ICBCApiParam.ApplyCredit applyCredit) {
        return null;
    }

    @PostMapping (value = "/fileNotice", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean fileNotice( ICBCApiParam.FileNotice fileNotice) {
        //记录回调信息
        MaterialDownHisDO materialDownHisDO = new MaterialDownHisDO();
        materialDownHisDO.setSerialNo(GeneratorIDUtil.execute());
        materialDownHisDO.setFileType(fileNotice.getReq().getFiletype());
        materialDownHisDO.setFileName(fileNotice.getReq().getFilesrc());
        materialDownHisDO.setStatus(IDict.K_JYZT.SUCCESS);
        int count = materialDownHisDOMapper.insertSelective(materialDownHisDO);
        Preconditions.checkArgument(count>0,"插入文件清单流水异常");
        ICBCApiParam.ReturnMsg returnMsg = new ICBCApiParam.ReturnMsg();
        returnMsg.getPub().setRetcode(IConstant.SUCCESS);
        returnMsg.getPub().setRetmsg("成功");
        return ResultBean.ofSuccess(returnMsg);

    }
}
