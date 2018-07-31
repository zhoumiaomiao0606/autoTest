package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InsuranceRisksParam;
import com.yunche.loan.domain.param.InsuranceUpdateParam;
import com.yunche.loan.domain.query.RiskQuery;
import com.yunche.loan.domain.vo.InsuranceDetailVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.RiskQueryVO;

import java.util.List;
import java.util.Map;

public interface InsuranceService {

    public RecombinationVO detail(Long orderId);

    public RecombinationVO query(Long orderId);

    public void update(InsuranceUpdateParam param);

    public InsuranceDetailVO riskDetail(Long orderId,Byte insuranceYear);

    public void riskInsert(InsuranceRisksParam param);

    public void riskUpdate(InsuranceRisksParam param);

    public void riskDetele(Long insuranceNumberId);

    public ResultBean<List<RiskQueryVO>> riskList(RiskQuery query);
}
