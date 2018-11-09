package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.google.gson.Gson;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.SecondHandCarEvaluateDO;
import com.yunche.loan.domain.entity.SecondHandCarVinDO;
import com.yunche.loan.domain.param.DrivinglicensePara;
import com.yunche.loan.domain.param.EvaluateParam;
import com.yunche.loan.domain.param.EvaluateWebParam;
import com.yunche.loan.domain.param.QueryVINParam;
import com.yunche.loan.domain.vo.CommonFinanceResult;
import com.yunche.loan.domain.vo.QueryCarTypeByVIN;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.SecondHandCarEvaluateDOMapper;
import com.yunche.loan.service.SecondHandCarService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;

@Service
public class SecondHandCarServiceImpl implements SecondHandCarService
{

    @Resource
    private BusinessReviewManager businessReviewManager;

    @Resource
    private SecondHandCarEvaluateDOMapper secondHandCarEvaluateDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    //2.orc行驶证识别
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

        //每次识别--若该合伙人下vin查询过则更新
        /*if (financeResult1!=null && financeResult1.getDatas()!=null)
        {
            financeResult1.getDatas().setQuery_time(new Date());

            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());
        }*/


        return ResultBean.ofSuccess(financeResult1);
    }

    //1.模糊查询vin码
    @Override
    public ResultBean queryVIN(QueryVINParam vin)
    {
        Preconditions.checkNotNull(vin.getOrderId(),"订单id不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(vin.getOrderId());

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());

        vin.setPartnerId(loanBaseInfoDO.getPartnerId());
        List<SecondHandCarEvaluateDO> list = secondHandCarEvaluateDOMapper.queryVIN(vin);

        return ResultBean.ofSuccess(list);
    }

    //3.根据vin码查询车型-----如果返回list为1则直接估价
    @Override
    public ResultBean queryCarTypeByVIN(String vin)
    {

        Map querys = new HashMap<>();
        querys.put("vin",vin);
        System.out.println("========");
        String financeResult = businessReviewManager.getFinanceUnisal("/market/car/vin",querys);

        CommonFinanceResult<List<QueryCarTypeByVIN>> financeResult1 = new CommonFinanceResult<List<QueryCarTypeByVIN>>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, financeResult1.getClass());
        }

        return ResultBean.ofSuccess(financeResult1.getDatas());

    }

    //4.估价接口---并保存信息
    @Override
    public ResultBean evaluate(EvaluateWebParam evaluateWebParam)
    {
        Preconditions.checkNotNull(evaluateWebParam.getOrderId(),"订单id不能为空");
        Preconditions.checkNotNull(evaluateWebParam.getPlate_num(),"车牌号不能为空");

        EvaluateParam param =new EvaluateParam();
        String financeResult = businessReviewManager.financeUnisal(param, "/market/car/evaluate");
        CommonFinanceResult<SecondHandCarVinDO> financeResult1 = new CommonFinanceResult<SecondHandCarVinDO>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, financeResult1.getClass());
        }

        //每次评估，查询到即保存
        /*if (financeResult1!=null && financeResult1.getDatas()!=null)
        {
            financeResult1.getDatas().setQuery_time(new Date());
            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());
        }*/
        return null;
    }

    @Override
    public ResultBean queryEvuluate(Long orderId)
    {
        Preconditions.checkNotNull(orderId,"订单id不能为空");

        return null;
    }
}
