package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.service.LoanProcessBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 三方过桥资金流程
 *
 * @author liuzhe
 * @date 2018/8/20
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanProcess/bridge")
public class LoanProcessBridgeController {


    @Autowired
    private LoanProcessBridgeService loanProcessBridgeService;


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
        return loanProcessBridgeService.approval(approval);
    }

    /**
     * 开启 [三方过桥资金]流程
     *
     * @param orderId
     * @return
     */
    @Limiter
    @GetMapping(value = "/startProcess")
    public ResultBean<Long> startProcess(@RequestParam(value = "orderId") Long orderId) {
        Long processId = loanProcessBridgeService.startProcess(orderId);
        return ResultBean.ofSuccess(processId);
    }
}
