package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.UniversalDataFlowDetailVO;
import com.yunche.loan.service.LoanDataFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/loanDataFlow", "/api/v1/app/loanDataFlow"})
public class LoanDataFlowController {

    @Autowired
    private LoanDataFlowService loanDataFlowService;


    @GetMapping(value = "/detail")
    public ResultBean<UniversalDataFlowDetailVO> detail(@RequestParam Long id) {
        return loanDataFlowService.detail(id);
    }

//    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultBean create(@RequestBody LoanDataFlowDO loanDataFlowDO) {
//        return loanDataFlowService.create(loanDataFlowDO);
//    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody LoanDataFlowDO loanDataFlowDO) {
        return loanDataFlowService.update(loanDataFlowDO);
    }

    @GetMapping(value = "/flowDept")
    public ResultBean<List<BaseVO>> flowOutDept() {
        return loanDataFlowService.flowDept();
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
