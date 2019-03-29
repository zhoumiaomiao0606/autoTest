package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditStructParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.service.CreditStructService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 结构化征信
 *
 * @author liuzhe
 * @date 2019/2/19
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/creditStruct")
public class CreditStructController {

    @Autowired
    private CreditStructService creditStructService;


    @GetMapping("/detail")
    public ResultBean<RecombinationVO> detail(@RequestParam Long orderId) {
        return ResultBean.ofSuccess(creditStructService.detail(orderId));
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean save(@RequestBody CreditStructParam param) {
        creditStructService.save(param);
        return ResultBean.ofSuccess();
    }
}
