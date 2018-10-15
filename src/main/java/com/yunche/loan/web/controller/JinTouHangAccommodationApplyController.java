package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;
import com.yunche.loan.service.JinTouHangAccommodationApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 金投行过桥处理
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/accommodation")
public class JinTouHangAccommodationApplyController {


    @Autowired
    private JinTouHangAccommodationApplyService accommodationApplyService;


    //--------------------------金投行过桥处理------------------------

//    @PostMapping(value = "/revoke", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    ResultBean revoke(@RequestBody AccommodationApplyParam param) {
//        return accommodationApplyService.revoke(param);
//    }

    /**
     * 批量导入借款
     *
     * @return
     */
    @PostMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional( rollbackFor = Exception.class)
    ResultBean reject(@RequestBody AccommodationApplyParam param) {
        return accommodationApplyService.reject(param);
    }


    /**
     * 单条贷款
     *
     * @return
     */
    @PostMapping(value = "/applyLoan", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional( rollbackFor = Exception.class)
    ResultBean applyLoan(@RequestBody AccommodationApplyParam param) {
        return accommodationApplyService.applyLoan(param);
    }


    /**
     * 批量贷款(作废)
     *
     * @return
     */
    @PostMapping(value = "/batchLoan", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional( rollbackFor = Exception.class)
    ResultBean batchLoan(@RequestBody AccommodationApplyParam param) {
        return accommodationApplyService.batchLoan(param);
    }
    /**
     * 批量导入借款
     *
     * @return
     */
    @GetMapping(value = "/batchImp")
    @Transactional( rollbackFor = Exception.class)
    ResultBean batchImp(@RequestParam("key") String key) {
        return accommodationApplyService.batchImp(key);
    }

    @PostMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean export(@RequestBody ExportApplyLoanPushParam param) {
        return accommodationApplyService.export(param);
    }

    //--------------------------金投行过桥处理------------------------

    //--------------------------金投行还款登记------------------------
    /*
    *查看还款记录信息
     */
    @GetMapping(value = "/calmoneydetail")
    ResultBean calMoneyDetail(@RequestParam("bridgeProcessId") Long bridgeProcessId,@RequestParam("orderId") Long orderId,@RequestParam("repayDate")String repayDate,@RequestParam("flag")String flag) {
        return accommodationApplyService.calMoneyDetail(bridgeProcessId,orderId,repayDate,flag);
    }
    /*
    计算费用
     */
    @GetMapping(value = "/calmoney")
    ResultBean calMoney(@RequestParam("bridgeProcessId") Long bridgeProcessId,@RequestParam("orderId") Long orderId,@RequestParam(" ")String repayDate) {
        return accommodationApplyService.calMoney(bridgeProcessId,orderId,repayDate);
    }

    /*
    判断是否有还款记录
     */
    @GetMapping(value = "/isreturn")
    ResultBean isReturn(@RequestParam("bridgeProcessId") Long bridgeProcessId,@RequestParam("orderId") Long orderId) {
        return accommodationApplyService.isReturn(bridgeProcessId,orderId);
    }

    /**
     * 异常还款
     *
     * @return
     */
    @PostMapping(value = "/abnormal", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean abnormalRepay(@RequestBody AccommodationApplyParam param) {
        return accommodationApplyService.abnormalRepay(param);
    }
    //--------------------------金投行还款登记------------------------


    //--------------------------金投行还款信息--------------------------

    /**
     * 金投行还款信息
     *
     * @return
     */
    @PostMapping(value = "/repayinfoexp", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean repayInfoExport(@RequestBody ExportApplyLoanPushParam param) {
        return accommodationApplyService.exportJinTouHangRepayInfo(param);
    }
    //--------------------------金投行还款信息--------------------------


    //--------------------------金投行还款登记--------------------------

    /**
     * 金投行息费登记
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/repayInterestRegister", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean repayInterestRegister(@RequestBody AccommodationApplyParam param) {
        return accommodationApplyService.repayInterestRegister(param);
    }

    /**
     * 金投行息费登记导出
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/interestRegisterExp", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean ExportRepayInterestRegister(@RequestBody ExportApplyLoanPushParam param) {
        return accommodationApplyService.exportJinTouHangInterestRegister(param);
    }
    //--------------------------金投行还款登记--------------------------


    @GetMapping("/detail")
    ResultBean detail(@RequestParam("bridgeProcessId") Long bridgeProcessId,@RequestParam("orderId") Long orderId) {
        return accommodationApplyService.detail(bridgeProcessId,orderId);
    }
}
