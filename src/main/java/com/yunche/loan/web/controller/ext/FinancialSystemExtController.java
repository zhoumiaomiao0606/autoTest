package com.yunche.loan.web.controller.ext;

import com.yunche.loan.config.result.ResultBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部-财务系统
 *
 * @author liuzhe
 * @date 2018/9/25
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/ext/financialSystem")
public class FinancialSystemExtController {


    @GetMapping("/list")
    public ResultBean list() {

        return ResultBean.ofSuccess(null);
    }
}
