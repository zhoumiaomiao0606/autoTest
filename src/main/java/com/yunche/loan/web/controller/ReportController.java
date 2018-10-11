package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BankCreditPrincipalQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.BankCreditPrincipalVO;
import com.yunche.loan.domain.vo.BusinessApprovalReportVO;
import com.yunche.loan.domain.vo.ContractSetReportVO;
import com.yunche.loan.domain.vo.TelBankCountVO;
import com.yunche.loan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/report", "/api/v1/app/report"})
public class ReportController {
    @Autowired
    private ReportService reportService;

    /*@GetMapping(value = "/businessapproval")
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
    }*/
    //合同套打

    @PostMapping(value = "/contractset",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<ContractSetReportVO>> contractSet(@RequestBody ContractSetQuery query) {
        return reportService.contractSet(query);
    }

    @PostMapping(value = "/contractsettotal",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean contractSetTotal(@RequestBody ContractSetQuery query){
        return ResultBean.ofSuccess(reportService.contractSetTotal(query));
    }
    @PostMapping(value = "/contractsetexport",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean contractSetExport(@RequestBody ContractSetQuery query) {
        return ResultBean.ofSuccess(reportService.contractSetExport(query));
    }

    //信用卡专项分期业务人行征信查询登记表--主贷
    @PostMapping(value = "/bankcreditprincipal",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<BankCreditPrincipalVO>> bankCreditPrincipal(@RequestBody BankCreditPrincipalQuery query) {
        return reportService.bankCreditPrincipal(query);
    }

    @PostMapping(value = "/bankcreditprincipaltotal",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean bankCreditPrincipalTotal(@RequestBody BankCreditPrincipalQuery query){
        return ResultBean.ofSuccess(reportService.bankCreditPrincipalTotal(query));
    }
    @PostMapping(value = "/bankcreditprincipalexport",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean bankCreditPrincipalExport(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.bankCreditPrincipalExport(query));
    }


    //信用卡专项分期业务人行征信查询登记表--全部
    @PostMapping(value = "/bankcreditall",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<BankCreditPrincipalVO>> bankCreditAll(@RequestBody BankCreditPrincipalQuery query) {
        return reportService.bankCreditAll(query);
    }

    @PostMapping(value = "/bankcreditalltotal",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean bankCreditAllTotal(@RequestBody BankCreditPrincipalQuery query){
        return ResultBean.ofSuccess(reportService.bankCreditAllTotal(query));
    }
    @PostMapping(value = "/bankcreditallexport",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean bankCreditAllExport(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.bankCreditAllExport(query));
    }

    //电审按银行分类统计--日报/周报/月报
    @PostMapping(value = "/telbankcount",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<TelBankCountVO>> telBankCount(@RequestBody BankCreditPrincipalQuery query) {
        return reportService.telBankCount(query);
    }

    @PostMapping(value = "/telbankcountexport",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telBankCountExport(@RequestBody BankCreditPrincipalQuery query) {
       return ResultBean.ofSuccess(reportService.telBankCountExport(query));
    }
    //电审按经办人统计表--日报/月报
    @PostMapping(value = "/telusercount",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telUserCount(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.telUserCount(query));
    }
    @PostMapping(value = "/telusercountexport",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telUserCountExport(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.telUserCountExport(query));
    }
    //电审按合伙人统计表--日报/周报/月报
    @PostMapping(value = "/telpartnercount",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telPartnerCount(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.telPartnerCount(query));
    }
    @PostMapping(value = "/telparcountexport",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telPartnerCountExport(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.telPartnerCountExport(query));
    }

    //电审客户明细(待开发)
    @PostMapping(value = "/telcustomerdetail",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telCustomerDetail(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.telCustomerDetail(query));
    }
    @PostMapping(value = "/telcustomerdetailexport",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean telCustomerDetailExport(@RequestBody BankCreditPrincipalQuery query) {
        return ResultBean.ofSuccess(reportService.telCustomerDetailExport(query));
    }

}
