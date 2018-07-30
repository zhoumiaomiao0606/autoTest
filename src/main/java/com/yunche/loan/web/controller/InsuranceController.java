package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InsuranceRisksParam;
import com.yunche.loan.domain.param.InsuranceUpdateParam;
import com.yunche.loan.domain.query.RiskQuery;
import com.yunche.loan.domain.vo.RiskQueryVO;
import com.yunche.loan.service.InsuranceService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/insurance")
public class InsuranceController {

    @Resource
    private InsuranceService insuranceService;

    /**
     * 车辆保险详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {

        return ResultBean.ofSuccess(insuranceService.detail(Long.valueOf(order_id)));
    }

    /**
     * 车辆保险详情
     */
    @GetMapping(value = "/query")
    public ResultBean query(@RequestParam String order_id) {

        return ResultBean.ofSuccess(insuranceService.query(Long.valueOf(order_id)));
    }

    /**
     * 录入车辆保险
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated InsuranceUpdateParam param) {
        insuranceService.update(param);
        return ResultBean.ofSuccess(null, "保存成功");
    }

    /**
     * 出险列表
     * @param jjq
     * @return
     */
    @PostMapping(value = "/risklist", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<RiskQueryVO>> riskList(@RequestBody RiskQuery query){
        return insuranceService.riskList(query);
    }
    /*
    * 出险详情
     */
    @GetMapping(value = "/riskdetail")
    public ResultBean riskDeatil(@RequestParam String order_id,@RequestParam String insurance_year){
        return ResultBean.ofSuccess(insuranceService.riskDetail(Long.valueOf(order_id),Byte.valueOf(insurance_year)));
    }
    /**
     * 出险插入
     */
    @PostMapping(value = "riskinsert", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean riskInsert(@RequestBody @Validated InsuranceRisksParam param){
        insuranceService.riskInsert(param);
        return ResultBean.ofSuccess(null, "保存成功");
    }
    /**
     * 出险更新
     */
    @PostMapping(value = "riskupdate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean riskUpdate(@RequestBody @Validated InsuranceRisksParam param){
        insuranceService.riskUpdate(param);
        return ResultBean.ofSuccess(null, "更新成功");
    }
    /**
     * 出险删除
     */
    @GetMapping(value = "riskdelete")
    public ResultBean riskDelete(@RequestParam("id") Long id) {
        insuranceService.riskDetele(id);
        return ResultBean.ofSuccess(null, "删除成功");
    }

}
