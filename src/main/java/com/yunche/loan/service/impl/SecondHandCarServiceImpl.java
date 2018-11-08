package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.google.gson.Gson;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.SecondHandCarVinDO;
import com.yunche.loan.domain.param.DrivinglicensePara;
import com.yunche.loan.domain.param.QueryVINParam;
import com.yunche.loan.domain.vo.CommonFinanceResult;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.SecondHandCarVinDOMapper;
import com.yunche.loan.service.SecondHandCarService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@Service
public class SecondHandCarServiceImpl implements SecondHandCarService
{

    @Resource
    private BusinessReviewManager businessReviewManager;

    @Resource
    private SecondHandCarVinDOMapper secondHandCarVinDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Override
    public ResultBean drivinglicense(DrivinglicensePara evaluationPara)
    {

        String financeResult = businessReviewManager.financeUnisal(evaluationPara, "/orc/drivinglicense");
        CommonFinanceResult<SecondHandCarVinDO> financeResult1 = new CommonFinanceResult<SecondHandCarVinDO>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
             financeResult1 = gson.fromJson(financeResult, financeResult1.getClass());
        }

        //每次评估，查询到即保存
        if (financeResult1!=null && financeResult1.getDatas()!=null)
        {
            financeResult1.getDatas().setQuery_time(new Date());
            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());
        }


        return ResultBean.ofSuccess(financeResult1);
    }

    @Override
    public ResultBean queryVIN(QueryVINParam vin)
    {
        Preconditions.checkNotNull(vin.getOrderId(),"订单id不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(vin.getOrderId());

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());


        List<SecondHandCarVinDO> list = secondHandCarVinDOMapper.queryVIN(vin);

        return ResultBean.ofSuccess(list);
    }

    @Override
    public ResultBean queryCarTypeByVIN(String vin) {
        return null;
    }
}
