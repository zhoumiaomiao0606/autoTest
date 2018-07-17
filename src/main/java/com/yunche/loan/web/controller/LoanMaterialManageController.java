package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanMaterialManageDO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.service.LoanMaterialManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/loanMaterialManage", "/api/v1/app/loanMaterialManage"})
public class LoanMaterialManageController {

    @Autowired
    private LoanMaterialManageService loanMaterialManageService;


    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> save(@RequestBody LoanMaterialManageDO loanMaterialManageDO) {
        return loanMaterialManageService.save(loanMaterialManageDO);
    }

    @GetMapping(value = "/detail")
    public ResultBean<RecombinationVO> detail(@RequestParam Long orderId) {
        return loanMaterialManageService.detail(orderId);
    }

    @GetMapping(value = "/imp")
    public ResultBean<Integer> imp(@RequestParam(value = "key") String ossKey) {
        return loanMaterialManageService.imp(ossKey);
    }
}
