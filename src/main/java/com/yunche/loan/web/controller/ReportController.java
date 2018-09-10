package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.BusinessApprovalReportVO;
import com.yunche.loan.domain.vo.ContractSetReportVO;
import com.yunche.loan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/report", "/api/v1/app/report"})
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping(value = "/businessapproval")
    public ResultBean<List<BusinessApprovalReportVO>> businessApproval(@RequestBody BaseQuery query) {
        return reportService.businessApproval(query);
    }
    @GetMapping(value = "/businessapprovaltotal")
    public ResultBean businessApprovalTotal(@RequestBody BaseQuery query){
        return ResultBean.ofSuccess(reportService.businessApprovalTotal(query));
    }
    @PostMapping(value = "/businessapprovalexport")
    public ResultBean businessApprovalExport(@RequestBody BaseQuery query) {
        return ResultBean.ofSuccess(reportService.businessApprovalExport(query));
    }

    @GetMapping(value = "/contractset")
    public ResultBean<List<ContractSetReportVO>> contractSet(@RequestBody ContractSetQuery query) {
        return reportService.contractSet(query);
    }

    @GetMapping(value = "/contractsettotal")
    public ResultBean contractSetTotal(@RequestBody ContractSetQuery query){
        return ResultBean.ofSuccess(reportService.contractSetTotal(query));
    }
    @PostMapping(value = "/contractsetexport")
    public ResultBean contractSetExport(@RequestBody ContractSetQuery query) {
        return ResultBean.ofSuccess(reportService.contractSetExport(query));
    }

}
