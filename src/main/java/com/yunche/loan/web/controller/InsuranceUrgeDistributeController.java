package com.yunche.loan.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ManualInsuranceParam;
import com.yunche.loan.domain.query.InsuranceListQuery;
import com.yunche.loan.domain.vo.InsuranceUrgeVO;
import com.yunche.loan.service.InsuranceUrgeDistributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 催收分配工作台
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/insuranceallot")
public class InsuranceUrgeDistributeController {

    @Autowired
    private InsuranceUrgeDistributeService insuranceUrgeDistributeService;

    /**
     * 催保列表
     * @param insuranceListQuery
     * @return
     */
    @PostMapping("/tasklist")
    public ResultBean taskList(@RequestBody InsuranceListQuery insuranceListQuery){

        PageHelper.startPage(insuranceListQuery.getPageIndex(), insuranceListQuery.getPageSize(), true);

        List list = insuranceUrgeDistributeService.list(insuranceListQuery);

        PageInfo<InsuranceUrgeVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    /**
     * 催保分配
     * @param param
     * @return
     */
    @PostMapping(value = "/manualDistribution", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> manualDistribution(@RequestBody ManualInsuranceParam param) {

        insuranceUrgeDistributeService.manualDistribution(param);

        return ResultBean.ofSuccess(null,"保存成功");
    }

    /**
     * 催保人员列表
     * @return
     */
    @GetMapping(value = "/insuranceUrgeEmployee")
    public ResultBean recordDetail() {

         List list = insuranceUrgeDistributeService.selectInsuranceDistributeEmployee();

        return ResultBean.ofSuccess(list);
    }

    /**
     * 催保分配详情页
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId){

        return insuranceUrgeDistributeService.detail(orderId);
    }
}
