package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ZhongAnCreditStructParam;
import com.yunche.loan.service.ZhongAnService;
import com.zhongan.scorpoin.common.ZhongAnOpenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2019/2/20
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/zhongAn")
public class ZhongAnController {

    @Autowired
    private ZhongAnService zhongAnService;


    @PostMapping(value = "/creditStruct", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean xx(@RequestBody ZhongAnCreditStructParam param) throws ZhongAnOpenException {
        return ResultBean.ofSuccess(zhongAnService.creditStruct(param));
    }

}
