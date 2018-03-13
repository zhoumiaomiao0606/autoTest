package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApplyLicensePlateRecordUpdateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/loanorder/applylicenseplaterecord")
public class ApplyLicensePlateRecordController {
    /**
     * 上牌记录详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return null;
    }


    /**
     * 上牌记录录入
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated ApplyLicensePlateRecordUpdateParam param) {
        return null;
    }


}
