package com.yunche.loan.web.controller;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.service.ExportQueryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/export"})
public class ExpertController
{
    @Resource
    private ExportQueryService exportQueryService;

    /**
     * EXCEl导出银行征信查询
     */
    @PostMapping(value = "/exportBankCreditQuery")
    public ResultBean exportBankCreditQuery(@RequestBody ExportBankCreditQueryVerifyParam exportBankCreditQueryVerifyParam)
    {
        Preconditions.checkNotNull(exportBankCreditQueryVerifyParam.getStartDate(), "开始时间不能为空");
        Preconditions.checkNotNull(exportBankCreditQueryVerifyParam.getEndDate(), "结束时间不能为空");
        return ResultBean.ofSuccess(exportQueryService.exportBankCreditQuery(exportBankCreditQueryVerifyParam));
    }

    /**
     * EXCEl导出社会征信查询
     */
    @PostMapping(value = "/exportSocialCreditQuery")
    public ResultBean expertSocialCreditQuery(@RequestBody ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam)
    {
        Preconditions.checkNotNull(exportSocialCreditQueryVerifyParam.getStartDate(), "开始时间不能为空");
        Preconditions.checkNotNull(exportSocialCreditQueryVerifyParam.getEndDate(), "结束时间不能为空");
        return ResultBean.ofSuccess(exportQueryService.expertSocialCreditQuery(exportSocialCreditQueryVerifyParam));
    }

    /**
     * EXCEl导出财务垫款明细查询
     */
    @PostMapping(value = "/exportRemitDetailQuery")
    public ResultBean expertRemitDetailQuery(@RequestBody ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam)
    {
        Preconditions.checkNotNull(exportRemitDetailQueryVerifyParam.getStartDate(), "开始时间不能为空");
        Preconditions.checkNotNull(exportRemitDetailQueryVerifyParam.getEndDate(), "结束时间不能为空");
        return ResultBean.ofSuccess(exportQueryService.expertRemitDetailQuery(exportRemitDetailQueryVerifyParam));
    }

    /**
     * EXCEl导出资料审核明细查询
     */
    @PostMapping(value = "/exportMaterialReviewQuery")
    public ResultBean expertMaterialReviewQuery(@RequestBody ExportMaterialReviewQueryVerifyParam exportMaterialReviewQueryVerifyParam)
    {
        Preconditions.checkNotNull(exportMaterialReviewQueryVerifyParam.getStartDate(), "开始时间不能为空");
        Preconditions.checkNotNull(exportMaterialReviewQueryVerifyParam.getEndDate(), "结束时间不能为空");
        return ResultBean.ofSuccess(exportQueryService.expertMaterialReviewQuery(exportMaterialReviewQueryVerifyParam));
    }

    /**
     * EXCEl导出抵押超期天数
     */
    @PostMapping(value = "/exportMortgageOverdueQuery")
    public ResultBean expertMortgageOverdueQuery(@RequestBody ExportMortgageOverdueQueryVerifyParam exportMortgageOverdueQueryVerifyParam)
    {
        Preconditions.checkNotNull(exportMortgageOverdueQueryVerifyParam.getStartDate(), "开始时间不能为空");
        Preconditions.checkNotNull(exportMortgageOverdueQueryVerifyParam.getEndDate(), "结束时间不能为空");
        return ResultBean.ofSuccess(exportQueryService.expertMortgageOverdueQuery(exportMortgageOverdueQueryVerifyParam));
    }

    /**
     * EXCEl导出抵押超期天数
     */
    @PostMapping(value = "/exportOrders")
    public ResultBean exportOrders(@RequestBody ExportOrdersParam exportOrdersParam)
    {
        return ResultBean.ofSuccess(exportQueryService.exportOrders(exportOrdersParam));
    }
}
