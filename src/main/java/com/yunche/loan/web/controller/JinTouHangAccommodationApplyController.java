package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;
import com.yunche.loan.service.JinTouHangAccommodationApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 金投行过桥处理
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/accommodation")
public class JinTouHangAccommodationApplyController {



    @Autowired
    private JinTouHangAccommodationApplyService accommodationApplyService;
    /**
     * 批量贷款
     * @return
     */
    @PostMapping(value = "/batchLoan", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean batchLoan(){
        return null;
    }

    @RequestMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean export(@RequestBody ExportApplyLoanPushParam param){
        return accommodationApplyService.export(param);
    }

    /**
     * 异常还款
     * @return
     */
    @RequestMapping(value = "/abnormal",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean abnormalRepay(AccommodationApplyParam param){
        return  accommodationApplyService.abnormalRepay(param);
    }

    /**
     * 金投行还款信息
     * @return
     */
    @RequestMapping(value = "/repayinfoexp", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean repayInfoExport(){
        return null;
    }

    @RequestMapping(value = "/repayInterestRegister", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean repayInterestRegister(){
        return null;
    }

}
