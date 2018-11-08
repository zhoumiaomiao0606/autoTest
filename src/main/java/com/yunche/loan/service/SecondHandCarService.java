package com.yunche.loan.service;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.EvaluationPara;

public interface SecondHandCarService
{
    ResultBean evaluation(EvaluationPara evaluationPara);
}
