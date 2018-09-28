package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.service.LoanProcessInsteadPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 代偿 -业务单流程
 *
 * @author liuzhe
 * @date 2018/8/20
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanProcess/insteadPay")
public class LoanProcessInsteadPayController {


    @Autowired
    private LoanProcessInsteadPayService loanProcessInsteadPayService;


    /**
     * 代偿流程 -审核
     *
     * @param approval
     * @return
     */
    @Limiter
    @PostMapping(value = "/approval", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> approval(@RequestBody ApprovalParam approval) {
        approval.setCheckPermission(true);
        approval.setNeedLog(true);
        approval.setNeedPush(true);
        approval.setAutoTask(false);
        return loanProcessInsteadPayService.approval(approval);
    }

}
