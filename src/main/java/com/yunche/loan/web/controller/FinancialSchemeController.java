package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.FinancialSchemeModifyUpdateParam;
import com.yunche.loan.domain.vo.FinancialSchemeVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.service.FinancialSchemeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/financialscheme")
public class FinancialSchemeController {

    @Resource
    private FinancialSchemeService financialSchemeService;


    /**
     * 金融方案详情
     */
    @GetMapping(value = "/detail")
    public ResultBean<RecombinationVO<FinancialSchemeVO>> detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(financialSchemeService.detail(Long.valueOf(order_id)));
    }

    /**
     * 金融方案电审详情
     */
    @GetMapping(value = "/verifyDetail")
    public ResultBean verifyDetail(@RequestParam String order_id, @RequestParam String his_id) {
        return ResultBean.ofSuccess(financialSchemeService.verifyDetail(Long.valueOf(order_id), Long.valueOf(his_id)));
    }

    /**
     * 金融方案修改详情
     */
    @GetMapping(value = "/modifyDetail")
    public ResultBean modifyDetail(@RequestParam String order_id, @RequestParam(required = false) String his_id) {
        return ResultBean.ofSuccess(financialSchemeService.modifyDetail(Long.valueOf(order_id), StringUtils.isBlank(his_id) ? null : Long.valueOf(his_id)));
    }

    /**
     * 金融方案修改详情
     */
    @Limiter("/api/v1/loanorder/financialscheme/modifyUpdate")
    @PostMapping(value = "/modifyUpdate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> modifyUpdate(@RequestBody @Validated FinancialSchemeModifyUpdateParam param) {
        return financialSchemeService.modifyUpdate(param);
    }

    /**
     * 查询客户名称 模糊查询
     */
    @GetMapping(value = "/queryModifyCustomerOrder")
    public ResultBean queryModifyCustomerOrder(@RequestParam(required = false) String name) {
        return ResultBean.ofSuccess(financialSchemeService.queryModifyCustomerOrder(name));
    }
}
