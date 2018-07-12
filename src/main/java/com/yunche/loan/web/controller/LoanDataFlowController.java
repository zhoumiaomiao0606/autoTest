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
                                              @RequestParam Byte type) {
        return loanDataFlowService.detail(orderId, type);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean create(@RequestBody LoanDataFlowDO loanDataFlowDO) {
        return loanDataFlowService.create(loanDataFlowDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody LoanDataFlowDO loanDataFlowDO) {
        return loanDataFlowService.update(loanDataFlowDO);
    }

    @GetMapping(value = "/key")
    public ResultBean<Object> key() {
        return loanDataFlowService.key();
    }

    @GetMapping(value = "/key-get-type")
    public ResultBean<Object> key_get_type(@RequestParam String key) {
        return loanDataFlowService.key_get_type(key);
    }

    @GetMapping(value = "/type-get-key")
    public ResultBean<Object> type_get_key(@RequestParam String type) {
        return loanDataFlowService.type_get_key(type);
    }
}
