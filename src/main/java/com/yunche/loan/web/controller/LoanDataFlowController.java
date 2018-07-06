package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.service.LoanDataFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanDataFlow")
public class LoanDataFlowController {

    @Autowired
    private LoanDataFlowService loanDataFlowService;


    @GetMapping(value = "/detail")
    public ResultBean<RecombinationVO> detail(@RequestParam Long orderId,
                                              @RequestParam String taskKey) {
        return loanDataFlowService.detail(orderId, taskKey);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean create(@RequestBody LoanDataFlowDO loanDataFlowDO) {
        return loanDataFlowService.create(loanDataFlowDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody LoanDataFlowDO loanDataFlowDO) {
        return loanDataFlowService.update(loanDataFlowDO);
    }

}
