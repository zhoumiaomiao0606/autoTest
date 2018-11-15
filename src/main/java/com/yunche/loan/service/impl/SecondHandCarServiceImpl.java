package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.SecondHandCarService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EmployeeService employeeService;

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Resource
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    //2.orc行驶证识别
    @Override
    public ResultBean drivinglicense(DrivinglicensePara evaluationPara)
    {

        String financeResult = businessReviewManager.financeUnisal2(evaluationPara, "/api/orc/drivinglicense");
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

        if (financeResult1.getDatas()!=null)
        {
            return ResultBean.ofSuccess(financeResult1.getDatas());
        }else
            {
                return ResultBean.ofError("orc识别失败");
            }


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
       /* String financeResult = businessReviewManager.getFinanceUnisal2("/api/car/vin",querys);*/

        String financeResult = "{\n" +
                "    \"datas\": [\n" +
                "        {\n" +
                "            \"makeId\": 28,\n" +
                "            \"modelId\": 4193,\n" +
                "            \"year\": 2014,\n" +
                "            \"manufacturerName\": \"东风悦达起亚\",\n" +
                "            \"manufacturerId\": 10043,\n" +
                "            \"dateOfProduction\": \"2014-12\",\n" +
                "            \"modelName\": \"K4（凯绅）\",\n" +
                "            \"manufacturerGuidePrice\": \"14.88\",\n" +
                "            \"name\": \"1.8L 自动 GLS\",\n" +
                "            \"makeInfo\": \"起亚牌YQZ7183A\",\n" +
                "            \"displacement\": 1.8,\n" +
                "            \"transmissionType\": \"手自一体\",\n" +
                "            \"id\": 111959,\n" +
                "            \"environmentProtectionStandard\": \"国5,国4\",\n" +
                "            \"makeName\": \"起亚\",\n" +
                "            \"styleColor\": \"极光黑\"\n" +
                "        },\n" +
                "\t    {\n" +
                "            \"makeId\": 28,\n" +
                "            \"modelId\": 4194,\n" +
                "            \"year\": 2014,\n" +
                "            \"manufacturerName\": \"东风悦达起亚\",\n" +
                "            \"manufacturerId\": 10043,\n" +
                "            \"dateOfProduction\": \"2014-12\",\n" +
                "            \"modelName\": \"车系A\",\n" +
                "            \"manufacturerGuidePrice\": \"14.88\",\n" +
                "            \"name\": \"车型A\",\n" +
                "            \"makeInfo\": \"起亚牌YQZ7183A\",\n" +
                "            \"displacement\": 1.8,\n" +
                "            \"transmissionType\": \"手自一体\",\n" +
                "            \"id\": 111960,\n" +
                "            \"environmentProtectionStandard\": \"国5,国4\",\n" +
                "            \"makeName\": \"起亚\",\n" +
                "            \"styleColor\": \"极光黑\"\n" +
                "        },\n" +
                "\t    {\n" +
                "            \"makeId\": 28,\n" +
                "            \"modelId\": 4194,\n" +
                "            \"year\": 2014,\n" +
                "            \"manufacturerName\": \"东风悦达起亚\",\n" +
                "            \"manufacturerId\": 10043,\n" +
                "            \"dateOfProduction\": \"2014-12\",\n" +
                "            \"modelName\": \"车系A\",\n" +
                "            \"manufacturerGuidePrice\": \"14.88\",\n" +
                "            \"name\": \"车型B\",\n" +
                "            \"makeInfo\": \"起亚牌YQZ7183A\",\n" +
                "            \"displacement\": 1.8,\n" +
                "            \"transmissionType\": \"手自一体\",\n" +
                "            \"id\": 111961,\n" +
                "            \"environmentProtectionStandard\": \"国5,国4\",\n" +
                "            \"makeName\": \"起亚\",\n" +
                "            \"styleColor\": \"极光黑\"\n" +
                "        },\n" +
                "\t    {\n" +
                "            \"makeId\": 29,\n" +
                "            \"modelId\": 4193,\n" +
                "            \"year\": 2014,\n" +
                "            \"manufacturerName\": \"东风悦达起亚\",\n" +
                "            \"manufacturerId\": 10043,\n" +
                "            \"dateOfProduction\": \"2014-12\",\n" +
                "            \"modelName\": \"K4（凯绅）\",\n" +
                "            \"manufacturerGuidePrice\": \"14.88\",\n" +
                "            \"name\": \"1.8L 自动 GLS\",\n" +
                "            \"makeInfo\": \"起亚牌YQZ7183A\",\n" +
                "            \"displacement\": 1.8,\n" +
                "            \"transmissionType\": \"手自一体\",\n" +
                "            \"id\": 111959,\n" +
                "            \"environmentProtectionStandard\": \"国5,国4\",\n" +
                "            \"makeName\": \"品牌A\",\n" +
                "            \"styleColor\": \"极光黑\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"resultCode\": 200\n" +
                "}";

        CommonFinanceResult<List<QueryCarTypeByVIN>> financeResult1 = new CommonFinanceResult<List<QueryCarTypeByVIN>>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, financeResult1.getClass());
        }

        if (financeResult1.getDatas() !=null){
            return ResultBean.ofSuccess(financeResult1.getDatas());
        }else {
            return ResultBean.ofError("vin码查询车型失败--");
        }


    }

    //4.估价接口---并保存信息
    @Override
    public ResultBean evaluate(EvaluateWebParam evaluateWebParam)
    {
        Preconditions.checkNotNull(evaluateWebParam.getOrderId(),"订单id不能为空");
        Preconditions.checkNotNull(evaluateWebParam.getPlate_num(),"车牌号不能为空");

        EvaluateParam param =new EvaluateParam();
        param.setCarCard(evaluateWebParam.getPlate_num());
        param.setBuyCarDate(evaluateWebParam.getRegister_date());
        param.setMileage(evaluateWebParam.getMileage());
        param.setTrimId(evaluateWebParam.getTrimId());
        String financeResult = businessReviewManager.financeUnisal2(param, "/api/car/evaluate");
        CommonFinanceResult<EvaluateVO> financeResult1 = new CommonFinanceResult<EvaluateVO>();
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
        if (financeResult1.getDatas()!=null)
        {
            //保存相关数据
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(evaluateWebParam.getOrderId());
            SecondHandCarEvaluateDO secondHandCarEvaluateDO =  BeanPlasticityUtills.copy(SecondHandCarEvaluateDO.class,evaluateWebParam);

            //保存订单相关合伙人团队----保存订单相关登陆业务员
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
            secondHandCarEvaluateDO.setParnter_id(loanBaseInfoDO.getPartnerId());

            secondHandCarEvaluateDOMapper.insertSelective(secondHandCarEvaluateDO);

            //更新绑定
            loanOrderDO.setSecond_hand_car_evuluate_id(secondHandCarEvaluateDO.getId());
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

            Long id = SessionUtils.getLoginUser().getId();
            secondHandCarEvaluateDO.setSaleman_id(id);

            return ResultBean.ofSuccess(financeResult1.getDatas());
        }else {
            return ResultBean.ofError("请求评估价信息出错");
        }

    }

    @Override
    public ResultBean queryEvuluate(Long orderId)
    {
        Preconditions.checkNotNull(orderId,"订单id不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO,"订单不存在");
        Long second_hand_car_evuluate_id = loanOrderDO.getSecond_hand_car_evuluate_id();
        //根据订单查询车辆信息
        //根据车辆信息里的估价信息id查询估价信息

            if (second_hand_car_evuluate_id !=null)
            {
                SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(second_hand_car_evuluate_id);
                return ResultBean.ofSuccess(secondHandCarEvaluateDO);
            }


            return ResultBean.ofError("该订单无二手车评估价信息");

    }

    @Override
    public ResultBean evaluateList(EvaluateListParam param)
    {

        //获取自身及管理的下属员工
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(SessionUtils.getLoginUser().getId());

        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(SessionUtils.getLoginUser().getId());

        param.setJuniorIds(juniorIds);

        param.setMaxGroupLevel(maxGroupLevel);

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List<SecondHandCarEvaluateList>  list =  secondHandCarEvaluateDOMapper.evaluateList(param);
        PageInfo<SecondHandCarEvaluateList> pageInfo = new PageInfo(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean queryEvuluateByEvuluateid(Long evuluateId)
    {
        SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(evuluateId);
        return ResultBean.ofSuccess(secondHandCarEvaluateDO);
    }

    @Override
    public ResultBean firstCarSite(FirstCarSiteParam param)
    {
        String financeResult = businessReviewManager.financeUnisal2(param, "/api/car/iautos");
        CommonFinanceResult<FirstCarSiteVO> financeResult1 = new CommonFinanceResult<FirstCarSiteVO>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, financeResult1.getClass());
        }

        FirstCarSiteWebVO firstCarSiteWebVO =new FirstCarSiteWebVO();
        return null;
    }
}
