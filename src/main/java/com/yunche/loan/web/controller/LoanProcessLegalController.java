package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.service.LoanProcessLegalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * [法务处理] -流程
 *
 * @author liuzhe
 * @date 2018/8/23
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanProcess/legal")
public class LoanProcessLegalController {

    @Autowired
    private LoanProcessLegalService loanProcessLegalService;


    /**
     * [法务处理]流程 -审核
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
        return loanProcessLegalService.approval(approval);
    }


    /**
     * 开启 [法务处理]流程
     *
     * @param orderId
     * @param bankRepayImpRecordId 批次号
     * @return
     */
    @Limiter
    @GetMapping(value = "/startProcess")
    public ResultBean<Long> startProcess(@RequestParam(value = "orderId") Long orderId,
                                         @RequestParam(value = "bankRepayImpRecordId") Long bankRepayImpRecordId) {
        Long processId = loanProcessLegalService.startProcess(orderId, bankRepayImpRecordId);
        return ResultBean.ofSuccess(processId);
    }
}
