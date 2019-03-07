package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InstallUpdateParam;
import com.yunche.loan.service.AuxiliaryService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/auxiliary")
public class AuxiliaryController {
    @Resource
    private AuxiliaryService auxiliaryService;
    /**
     * 确认收到钥匙
     */
    @GetMapping(value = "/commit")
    public ResultBean commit(@RequestParam String order_id) {
        auxiliaryService.commit(Long.valueOf(order_id));
        return ResultBean.ofSuccess(null,"确认成功");
    }

    /**
     * 查看gps列表
     */
    @GetMapping(value = "/query")
    public ResultBean query(@RequestParam String order_id) {
        return ResultBean.ofSuccess(auxiliaryService.query(Long.valueOf(order_id)));
    }

    /**
     * 查看第三方gps列表
     */
    @GetMapping(value = "/queryjimi")
    public ResultBean queryOther(@RequestParam String partner_name) {
        return ResultBean.ofSuccess(auxiliaryService.queryJimi(partner_name));
    }

    /**
     * 安装gps
     */
    @PostMapping(value = "/install", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean install(@RequestBody @Validated InstallUpdateParam param) {
        auxiliaryService.install(param,0);
        return ResultBean.ofSuccess(null,"安装成功");
    }

    /**
     * gps详细
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(auxiliaryService.detail(Long.valueOf(order_id)));
    }

    /**
     * gps详细
     */
    @GetMapping(value = "/appdetail")
    public ResultBean appDetail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(auxiliaryService.appDetail(Long.valueOf(order_id)));
    }

    /**
     * gps可用查询
     */
    @PostMapping(value = "/gpsAvailable")
    public ResultBean gpsAvailable(@RequestBody InstallUpdateParam param) {
        return auxiliaryService.gpsAvailable(param);
    }
}
