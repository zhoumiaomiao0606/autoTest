package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.FinancialProductVO;
import com.yunche.loan.service.FinancialProductService;
import com.yunche.loan.service.LoanProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param custBaseInfoVO
     */
    @PostMapping(value = "/creditApply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultBean<Void> creditApply(@RequestBody CustBaseInfoVO custBaseInfoVO, @RequestParam("processId") String processId) {
        return loanProcessService.creditApply(custBaseInfoVO, processId);
    }

}


