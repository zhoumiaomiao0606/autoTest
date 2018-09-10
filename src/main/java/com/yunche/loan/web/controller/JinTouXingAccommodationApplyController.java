package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 金投行过桥处理
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/accommodation")
public class JinTouXingAccommodationApplyController {
    /**
     * 批量贷款
     * @return
     */
    @PostMapping(value = "/batchLoan", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean batchLoan(){
        return null;
    }

    @RequestMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean export(){
        return null;
    }

    @RequestMapping(value = "/abnormal",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResultBean abnormalRepay(){
        return null;
    }

}
