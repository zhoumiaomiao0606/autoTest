package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.service.LoanProcessCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 催收工作台 -业务单流程
 *
 * @author liuzhe
 * @date 2018/8/20
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanProcess/collection")
public class LoanProcessCollectionController {

    @Autowired
    private LoanProcessCollectionService loanProcessCollectionService;


    /**
     * 催收工作台流程 -审核
     *
     * @param approval
     * @return
     */
    @Limiter("/api/v1/loanProcess/collection/approval")
    @PostMapping(value = "/approval", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> approval(@RequestBody ApprovalParam approval) {
        approval.setCheckPermission(true);
        approval.setNeedLog(true);
        approval.setNeedPush(true);
        return loanProcessCollectionService.approval(approval);
    }

}
