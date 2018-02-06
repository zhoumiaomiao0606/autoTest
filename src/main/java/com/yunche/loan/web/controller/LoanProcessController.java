package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.LoanProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhouguoliang on 2018/1/30.
 */
@CrossOrigin
@RestController
@RequestMapping("/loanProcess")
public class LoanProcessController {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessController.class);

    @Autowired
    private LoanProcessService loanProcessService;

    /**
     * 合伙人在[发起]征信申请时调用该服务
     * @param operatorId
     * @param operatorName
     * @param operatorRole
     * @return
     */
    @GetMapping(value = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<String> start(@RequestParam("operatorId") Long operatorId,
                                    @RequestParam("operatorName") String operatorName,
                                    @RequestParam("operatorRole") String operatorRole) {
        return loanProcessService.startProcessInstance(operatorId, operatorName, operatorRole);
    }

    /**
     * 合伙人[提交]征信申请表单时调用该服务
     * @param instLoanOrderVO
     * @param processId
     * @return
     */
    @PostMapping(value = "/creditApply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> creditApply(@RequestBody InstLoanOrderVO instLoanOrderVO, @RequestParam("processId") String processId,
                                        @RequestParam("operatorId") Long operatorId,
                                        @RequestParam("operatorName") String operatorName,
                                        @RequestParam("operatorRole") String operatorRole) {
        return loanProcessService.creditApply(instLoanOrderVO, processId, operatorId, operatorName, operatorRole);
    }

    /**
     * 内勤人员[审核]征信申请
     * @param processId
     * @param action
     * @return
     */
    @GetMapping(value = "/creditVerify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> creditVerify(@RequestParam("processId") String processId, @RequestParam("action") String action,
                                         @RequestParam("operatorId") Long operatorId,
                                         @RequestParam("operatorName") String operatorName,
                                         @RequestParam("operatorRole") String operatorRole) {
        return loanProcessService.creditVerify(processId, action, operatorId, operatorName, operatorRole);
    }


    /**
     * 征信专员[录入]银行征信记录
     * @param custBaseInfoVO
     * @param processId
     * @param action
     * @return
     */
    @PostMapping(value = "/bankCreditRecord", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> bankCreditRecord(@RequestBody CustBaseInfoVO custBaseInfoVO, @RequestParam("processId") String processId,
                                             @RequestParam("action") String action,
                                             @RequestParam("operatorId") Long operatorId,
                                             @RequestParam("operatorName") String operatorName,
                                             @RequestParam("operatorRole") String operatorRole) {
        return loanProcessService.bankCreditRecord(custBaseInfoVO, processId, action, operatorId, operatorName, operatorRole);
    }

    /**
     * 征信专员[录入]社会征信记录
     * @param custBaseInfoVO
     * @param processId
     * @param action
     * @return
     */
    @PostMapping(value = "/socialCreditRecord", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> socialCreditRecord(@RequestBody CustBaseInfoVO custBaseInfoVO, @RequestParam("processId") String processId,
                                               @RequestParam("action") String action,
                                               @RequestParam("operatorId") Long operatorId,
                                               @RequestParam("operatorName") String operatorName,
                                               @RequestParam("operatorRole") String operatorRole) {
        return loanProcessService.socialCreditRecord(custBaseInfoVO, processId, action, operatorId, operatorName, operatorRole);
    }
}


