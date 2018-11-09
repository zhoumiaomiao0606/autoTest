package com.yunche.loan.service;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.DrivinglicensePara;
import com.yunche.loan.domain.param.EvaluateParam;
import com.yunche.loan.domain.param.EvaluateWebParam;
import com.yunche.loan.domain.param.QueryVINParam;

public interface SecondHandCarService
{
    ResultBean drivinglicense(DrivinglicensePara evaluationPara);

    ResultBean queryVIN(QueryVINParam vin);

    ResultBean queryCarTypeByVIN(String vin);

    ResultBean evaluate(EvaluateWebParam evaluateWebParam);

    ResultBean queryEvuluate(Long orderId);
}
