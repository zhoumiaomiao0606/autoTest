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
     */
    @PostMapping(value = "/creditApply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> creditApply(@RequestBody InstLoanOrderVO instLoanOrderVO, @RequestParam("processId") String processId) {
        return loanProcessService.creditApply(instLoanOrderVO, processId);
    }

    public ResultBean<Void> creditVerify(@RequestParam("processId") String processId, @RequestParam("action") String action) {
        return loanProcessService.creditVerify(processId, action);
    }

}


