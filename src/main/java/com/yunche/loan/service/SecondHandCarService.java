package com.yunche.loan.service;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;

public interface SecondHandCarService
{
    ResultBean drivinglicense(DrivinglicensePara evaluationPara);

    ResultBean queryVIN(QueryVINParam vin);

    ResultBean queryCarTypeByVIN(String vin);

    ResultBean evaluate(EvaluateWebParam evaluateWebParam);

    ResultBean queryEvuluate(Long orderId);

    ResultBean evaluateList(EvaluateListParam param);

    ResultBean queryEvaluateByEvaluateid(Long evaluateId);

    ResultBean firstCarSite(FirstCarSiteParam param);
}
