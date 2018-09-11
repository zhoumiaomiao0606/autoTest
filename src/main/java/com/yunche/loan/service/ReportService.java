package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.BusinessApprovalReportTotalVO;
import com.yunche.loan.domain.vo.BusinessApprovalReportVO;
import com.yunche.loan.domain.vo.ContractSetReportTotalVO;
import com.yunche.loan.domain.vo.ContractSetReportVO;

import java.util.List;

public interface ReportService {
    ResultBean<List<BusinessApprovalReportVO>> businessApproval(BaseQuery query);

    BusinessApprovalReportTotalVO businessApprovalTotal(BaseQuery query);

    String businessApprovalExport(BaseQuery query);

    ResultBean<List<ContractSetReportVO>> contractSet(ContractSetQuery query);

    ContractSetReportTotalVO contractSetTotal(ContractSetQuery query);

    String contractSetExport(ContractSetQuery query);
}
