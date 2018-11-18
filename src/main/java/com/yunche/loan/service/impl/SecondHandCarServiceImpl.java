package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunche.loan.config.exception.BizException;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
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

    @Resource
    private PartnerRelaEmployeeDOMapper partnerRelaEmployeeDOMapper;

    @Resource
    private SecondHandCarVinDOMapper secondHandCarVinDOMapper;

    //2.orc行驶证识别
    @Override
    public ResultBean drivinglicense(DrivinglicensePara evaluationPara)
    {

        String financeResult = businessReviewManager.financeUnisal2(evaluationPara, "/api/orc/drivinglicense");
        CommonFinanceResult<SecondHandCarVinDO> financeResult1 = new CommonFinanceResult<SecondHandCarVinDO>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Type type =new TypeToken<CommonFinanceResult<SecondHandCarVinDO>>(){}  .getType();
            Gson gson = new Gson();
             financeResult1 = gson.fromJson(financeResult, type);
        }

        //每次识别--若该合伙人下vin查询过则更新
        /*if (financeResult1!=null && financeResult1.getDatas()!=null)
        {
            financeResult1.getDatas().setQuery_time(new Date());

            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());
        }*/


        if (financeResult1.getDatas()!=null)
        {
            //判断vin码是否有查询记录
            if(financeResult1.getDatas().getVin()!=null &&!"".equals(financeResult1.getDatas().getVin()))
            {
                QueryVINParam vin =new QueryVINParam();
                vin.setQueryVIN(financeResult1.getDatas().getVin());
                //获取自身及管理的下属员工
                Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(SessionUtils.getLoginUser().getId());

                Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(SessionUtils.getLoginUser().getId());

                vin.setJuniorIds(juniorIds);

                vin.setMaxGroupLevel(maxGroupLevel);

                List<SecondHandCarEvaluateList> secondHandCarEvaluateLists = secondHandCarEvaluateDOMapper.selectVinHistory(vin);
                if (secondHandCarEvaluateLists !=null)
                {
                    throw  new BizException("该vin码七天内有查过估价信息");
                }

            }


            financeResult1.getDatas().setQuery_time(new Date());

            financeResult1.getDatas().setSaleman_id(SessionUtils.getLoginUser().getId());

            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());

            SecondHandCarVinDO datas = financeResult1.getDatas();
            if(datas.getRegister_date().length() !=8)
            {
                System.out.println("时间长度==="+ datas.getRegister_date().length());
                financeResult1.getDatas().setRegister_date(null);
            }
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

        //获取自身及管理的下属员工
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(SessionUtils.getLoginUser().getId());

        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(SessionUtils.getLoginUser().getId());

        vin.setJuniorIds(juniorIds);

        vin.setMaxGroupLevel(maxGroupLevel);

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
            Type type =new TypeToken<CommonFinanceResult<List<QueryCarTypeByVIN>>>(){}  .getType();
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, type);
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

        Preconditions.checkNotNull(evaluateWebParam.getPlate_num(),"车牌号不能为空");
        Preconditions.checkNotNull(evaluateWebParam.getMileage(),"公里数不能为空");
        Preconditions.checkNotNull(evaluateWebParam.getRegister_date(),"上牌时间不能为空");

        //Preconditions.checkNotNull(evaluateWebParam.getOrderId(),"订单id不能为空");
        //判断orc



        EvaluateParam param =new EvaluateParam();

         param.setCarCard(evaluateWebParam.getPlate_num());

        param.setBuyCarDate(evaluateWebParam.getRegister_date());
        param.setMileage(evaluateWebParam.getMileage());
        param.setTrimId(evaluateWebParam.getTrimId());
        String financeResult = businessReviewManager.financeUnisal2(param, "/api/car/evaluate");
        CommonFinanceResult<EvaluateVO> financeResult1 = new CommonFinanceResult<EvaluateVO>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Type type =new TypeToken<CommonFinanceResult<EvaluateVO>>(){}  .getType();
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, type);
        }

        //每次评估，查询到即保存
        /*if (financeResult1!=null && financeResult1.getDatas()!=null)
        {
            financeResult1.getDatas().setQuery_time(new Date());
            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());
        }*/
        if (financeResult1.getDatas()!=null)
        {
            SecondHandCarEvaluateDO secondHandCarEvaluateDO =  BeanPlasticityUtills.copy(SecondHandCarEvaluateDO.class,evaluateWebParam);

            //保存当前业务员
            secondHandCarEvaluateDO.setSaleman_id(SessionUtils.getLoginUser().getId());

            //业务员团队
            Long partnerIdByEmployeeId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(SessionUtils.getLoginUser().getId());

            //设置合伙人团队
            secondHandCarEvaluateDO.setParnter_id(partnerIdByEmployeeId);

            //设置查询的评估价
            secondHandCarEvaluateDO.setEvaluate_price(new BigDecimal(financeResult1.getDatas().getB2CPrices().getB().getMid()));

            //设置当前查询时间
            secondHandCarEvaluateDO.setQuery_time(new Date());

            secondHandCarEvaluateDO.setEvaluate_json(financeResult);

            secondHandCarEvaluateDOMapper.insertSelective(secondHandCarEvaluateDO);

            if (evaluateWebParam.getOrderId()!=null)
            {
                //绑定订单

                //保存相关数据
                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(evaluateWebParam.getOrderId());

                //更新绑定
                loanOrderDO.setSecond_hand_car_evaluate_id(secondHandCarEvaluateDO.getId());

                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            }


               return ResultBean.ofSuccess(secondHandCarEvaluateDO.getId());

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
        Long second_hand_car_evaluate_id = loanOrderDO.getSecond_hand_car_evaluate_id();
        //根据订单查询车辆信息
        //根据车辆信息里的估价信息id查询估价信息

            if (second_hand_car_evaluate_id !=null)
            {
                SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(second_hand_car_evaluate_id);
                //解析估价信息
                if (secondHandCarEvaluateDO!=null && secondHandCarEvaluateDO.getEvaluate_json()!=null && !"".equals(secondHandCarEvaluateDO.getEvaluate_json()))
                {
                    CommonFinanceResult<EvaluateVO> financeResult1 = new CommonFinanceResult<EvaluateVO>();

                        Type type =new TypeToken<CommonFinanceResult<EvaluateVO>>(){}  .getType();
                        Gson gson = new Gson();
                        financeResult1 = gson.fromJson(secondHandCarEvaluateDO.getEvaluate_json(), type);

                        if (financeResult1!=null && financeResult1.getDatas()!=null)
                        {
                            secondHandCarEvaluateDO.setB2CPrices(financeResult1.getDatas().getB2CPrices());
                            secondHandCarEvaluateDO.setC2BPrices(financeResult1.getDatas().getC2BPrices());
                        }

                }
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
    public ResultBean queryEvaluateByEvaluateid(Long evaluateId)
    {
        SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(evaluateId);
        return ResultBean.ofSuccess(secondHandCarEvaluateDO);
    }

    //是否每次查询最新的
    @Override
    public ResultBean firstCarSite(FirstCarSiteParam param)
    {
        Preconditions.checkNotNull(param.getOrderId(),"订单id不能为空");
        //根据订单id获取关联的vin码---获取地址
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(param.getOrderId());
        if (loanOrderDO.getSecond_hand_car_evaluate_id()==null)
        {
            throw new BizException("该订单无关联二手车vin码信息");
        }
        SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanOrderDO.getSecond_hand_car_evaluate_id());
        if (secondHandCarEvaluateDO == null)
        {
            throw new BizException("该订单绑定的估价信息有误");
        }

        //设置请求参数vin码
        param.setVin(secondHandCarEvaluateDO.getVin());

        //上牌地待讨论

        String financeResult = businessReviewManager.financeUnisal2(param, "/api/car/iautos");
        CommonFinanceResult<FirstCarSiteVO> financeResult1 = new CommonFinanceResult<FirstCarSiteVO>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Type type =new TypeToken<CommonFinanceResult<FirstCarSiteVO>>(){}  .getType();
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, type);
        }

        if (financeResult1.getDatas()!=null)
        {
            //设置返回值
        }

        FirstCarSiteWebVO firstCarSiteWebVO =new FirstCarSiteWebVO();
        return ResultBean.ofSuccess(firstCarSiteWebVO);
    }
}
