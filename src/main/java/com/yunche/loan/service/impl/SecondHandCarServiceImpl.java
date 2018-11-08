package com.yunche.loan.service.impl;

import com.google.gson.Gson;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.SecondHandCarVinDO;
import com.yunche.loan.domain.param.EvaluationPara;
import com.yunche.loan.domain.vo.CommonFinanceResult;
import com.yunche.loan.domain.vo.FinanceResult;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.service.SecondHandCarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SecondHandCarServiceImpl implements SecondHandCarService
{

    @Resource
    private BusinessReviewManager businessReviewManager;

    @Override
    public ResultBean evaluation(EvaluationPara evaluationPara)
    {

        String financeResult = businessReviewManager.financeUnisal(evaluationPara, "/orc/drivinglicense");
        CommonFinanceResult<SecondHandCarVinDO> financeResult1 = new CommonFinanceResult<SecondHandCarVinDO>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
             financeResult1 = gson.fromJson(financeResult, financeResult1.getClass());
        }

        //

        return ResultBean.ofSuccess(financeResult1);
    }
}
