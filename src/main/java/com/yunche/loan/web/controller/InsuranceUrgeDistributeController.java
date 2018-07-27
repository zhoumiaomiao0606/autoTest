package com.yunche.loan.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ManualDistributionBaseParam;
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
@RequestMapping("/api/v1/loanorder/insuranceurge")
public class InsuranceUrgeDistributeController {

    @Autowired
    private InsuranceUrgeDistributeService insuranceUrgeDistributeService;

    /**
     * 催保列表
     * @param pageIndex
     * @param pageSize
     * @param taskStatus  1:未分配列表  2：已分配列表
     */
    @GetMapping("/tasklist")
    public ResultBean taskList(@RequestParam("pageIndex")Integer pageIndex, @RequestParam("pageSize")Integer pageSize,@RequestParam("taskStatus") Byte taskStatus){

        PageHelper.startPage(pageIndex, pageSize, true);

        List list = insuranceUrgeDistributeService.list(pageIndex, pageSize, taskStatus);

        PageInfo<InsuranceUrgeVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    /**
     * 催保分配
     * @param param
     * @return
     */
    @PostMapping(value = "/manualDistribution", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> manualDistribution(@RequestBody  ManualDistributionBaseParam param) {

        insuranceUrgeDistributeService.manualDistribution(param.getManual_distribution_list());

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

}
