package com.yunche.loan.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.InsuranceListQuery;
import com.yunche.loan.domain.vo.InsuranceUrgeVO;
import com.yunche.loan.service.InsuranceUrgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/insuranceurge")
public class InsuranceUrgeController {

    @Autowired
    private InsuranceUrgeService insuranceUrgeService;

    /**
     *
     * @return
     */
    @PostMapping("/list")
    public ResultBean list(@RequestBody InsuranceListQuery insuranceListQuery){

        PageHelper.startPage(insuranceListQuery.getPageIndex(), insuranceListQuery.getPageSize(), true);

        List list = insuranceUrgeService.list(insuranceListQuery);

        PageInfo<InsuranceUrgeVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }


    /**
     * 催保分配详情页
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId){

        return insuranceUrgeService.detail(orderId);
    }


}
