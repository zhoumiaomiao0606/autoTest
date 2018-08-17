package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.service.LoanApplicationCompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 申请代偿
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/compensation")
public class LoanApplyCompensationController {

    @Autowired
    private LoanApplicationCompensationService loanApplicationCompensationService;

    /**
     * 批量导入
     * @param key
     * @return
     */
    @GetMapping("/import")
    public ResultBean batchInsert(@RequestParam("key") String key){
        loanApplicationCompensationService.batchInsert(key);
        return ResultBean.ofSuccess(null,"导入成功");
    }

    /**
     * 手工添加
     * @param param
     * @return
     */
    @PostMapping(value = "/manualInsert" ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean manualInsert(@RequestBody UniversalCompensationParam param){
        loanApplicationCompensationService.manualInsert(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }

    /**
     * 详情展示
     * @param query
     * @return
     */
    @PostMapping(value = "/detail" ,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean detail(@RequestBody UniversalCompensationQuery query){
        return loanApplicationCompensationService.detail(query);
    }

}

