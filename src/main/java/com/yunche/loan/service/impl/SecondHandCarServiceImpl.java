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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

@Service
public class SecondHandCarServiceImpl implements SecondHandCarService
{
    private static final Logger LOG = LoggerFactory.getLogger(SecondHandCarServiceImpl.class);
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

    @Resource
    private SecondHandCarFirstSiteMapper secondHandCarFirstSiteMapper;

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

        if (!financeResult1.getResultCode().trim().equals("200"))
        {
            return ResultBean.ofError("ocr识别失败");
        }

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

                /*List<SecondHandCarEvaluateList> secondHandCarEvaluateLists = secondHandCarEvaluateDOMapper.selectVinHistory(vin);
                if (secondHandCarEvaluateLists !=null || secondHandCarEvaluateLists.size()!=0)
                {
                    throw  new BizException("该vin码七天内有查过估价信息");
                }*/

            }


            financeResult1.getDatas().setQuery_time(new Date());

            financeResult1.getDatas().setSaleman_id(SessionUtils.getLoginUser().getId());

            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());

            SecondHandCarVinDO datas = financeResult1.getDatas();
            if(datas!=null && datas.getRegister_date()!=null && !"".equals(datas.getRegister_date()) && datas.getRegister_date().length() !=8)
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
        String financeResult = businessReviewManager.getFinanceUnisal2("/api/car",querys);

       /* String financeResult = "{\n" +
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
                "}";*/

        CommonFinanceResult<List<QueryCarTypeByVIN>> financeResult1 = new CommonFinanceResult<List<QueryCarTypeByVIN>>();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Type type =new TypeToken<CommonFinanceResult<List<QueryCarTypeByVIN>>>(){}  .getType();
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, type);
        }

        if (!financeResult1.getResultCode().equals("200"))
        {
            return ResultBean.ofError("vin码查询车型失败");
        }

        if (financeResult1.getDatas().size()!=0)
        {
            //将车型code  重新赋值车型id

            return ResultBean.ofSuccess(financeResult1.getDatas());
        }else {
            return ResultBean.ofError("vin码查询车型失败-或该vin码无对应车型-");
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

        if (!financeResult1.getResultCode().equals("200"))
        {
            return ResultBean.ofError("估价失败");
        }

        //每次评估，查询到即保存
        /*if (financeResult1!=null && financeResult1.getDatas()!=null)
        {
            financeResult1.getDatas().setQuery_time(new Date());
            secondHandCarVinDOMapper.insertSelective(financeResult1.getDatas());
        }*/
        if (financeResult1.getDatas()!=null)
        {
            /*SecondHandCarEvaluateDO secondHandCarEvaluateDO =new SecondHandCarEvaluateDO();*/
                /*BeanUtils.copyProperties(evaluateWebParam,secondHandCarEvaluateDO);
                //时间格式
            secondHandCarEvaluateDO.setRegister_date(new Date(evaluateWebParam.getRegister_date()));*/
            SecondHandCarEvaluateDO secondHandCarEvaluateDO = BeanPlasticityUtills.copy(SecondHandCarEvaluateDO.class, evaluateWebParam);


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
            return ResultBean.ofError("请求评估价信息失败或无该车辆估价信息");
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
                    if (secondHandCarEvaluateDO.getRegister_date()!=null && secondHandCarEvaluateDO.getQuery_time()!=null)
                    {
                        ZoneId zone = ZoneId.systemDefault();

                        Instant instant1 = secondHandCarEvaluateDO.getRegister_date().toInstant();
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant1, zone);
                        LocalDate localDate1 = localDateTime.toLocalDate();

                        Instant instant2 = secondHandCarEvaluateDO.getQuery_time().toInstant();
                        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(instant2, zone);
                        LocalDate localDate2 = localDateTime.toLocalDate();

                        Period period =Period.between(localDate1,localDate2);

                        StringBuilder stringBuilder =new StringBuilder();
                        stringBuilder.append(period.getYears()).append("年 ").append(period.getMonths()).append("月 ").append(period.getDays()).append("日");
                        secondHandCarEvaluateDO.setSecondCarUserdTime(stringBuilder.toString());
                    }

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

    public  String getCityName(String plateArea)
    {

        int i = plateArea.lastIndexOf("市");

        if (i==plateArea.length()-1)
        {
            return plateArea.substring(0,i);
        }else
        {
          return plateArea;
        }
    }

    //是否每次查询最新的
    @Override
    public ResultBean firstCarSite(FirstCarSiteParam param){
        Preconditions.checkNotNull(param.getOrderId(), "订单id不能为空");
        //根据订单id获取关联的vin码---获取地址
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(param.getOrderId());
        if (loanOrderDO.getSecond_hand_car_evaluate_id() == null) {
            throw new BizException("该订单无关联二手车vin码信息");
        }
        SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanOrderDO.getSecond_hand_car_evaluate_id());
        if (secondHandCarEvaluateDO == null) {
            throw new BizException("该订单绑定的估价信息有误");
        }

        //判断注册日期
        String date = "2014-01-01 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (secondHandCarEvaluateDO.getRegister_date()!=null && secondHandCarEvaluateDO.getRegister_date().after(sdf.parse(date)))
            {
                throw  new BizException("注册日期<2014-01-01才能查询第一车网估价");
            }
        } catch (ParseException e) {
            throw  new BizException("时间转换异常");
        }

        //如果订单已经有绑定信息则取出来解析
        if (loanOrderDO.getSecond_hand_car_first_site_id() == null) {

            //设置请求参数vin码
            param.setVin(secondHandCarEvaluateDO.getVin());

            //上牌地待讨论
            //根据订单号取出上牌地
            String plateArea =  secondHandCarEvaluateDOMapper.selectPlateAreaByOrderId(param.getOrderId());
            if ("市辖区".equals(plateArea))
            {
                plateArea =  secondHandCarEvaluateDOMapper.selectPlateAreaParentByOrderId(param.getOrderId());
            }
            System.out.println("====="+plateArea);
            if (plateArea ==null || "".equals(plateArea))
            {
                throw new BizException("该订单上牌地无填写");
            }

            param.setCityName(getCityName(plateArea));


            LOG.info(param.toString());

            String financeResult = businessReviewManager.financeUnisal2(param, "/api/car/iautos");
           /* String financeResult = "{\n" +
                    "    \"datas\": [\n" +
                    "        {\n" +
                    "            \"Brand\": \"起亚\",\n" +
                    "            \"Series\": \"起亚K4\",\n" +
                    "            \"NewPrice\": {\n" +
                    "                \"Price\": \"14.88\",\n" +
                    "                \"ModelID\": \"149435\"\n" +
                    "            },\n" +
                    "            \"Color\": \"极光黑\",\n" +
                    "            \"Model\": {\n" +
                    "                \"ProductionYear\": \"2014\",\n" +
                    "                \"VersionDate\": \"201409\",\n" +
                    "                \"MergeID\": \"149435\",\n" +
                    "                \"Price\": \"14.8800\",\n" +
                    "                \"TransmissionType\": \"6档 手自一体\",\n" +
                    "                \"ID\": \"149435\",\n" +
                    "                \"VersionYear\": \"2014\",\n" +
                    "                \"Displacement\": \"1.8\",\n" +
                    "                \"Name\": \"起亚K4-1.8-A/MT-GLS(国Ⅳ)\"\n" +
                    "            },\n" +
                    "            \"UsedPrice\": {\n" +
                    "                \"SellPrice\": \"8.00\",\n" +
                    "                \"BuyPrice\": \"7.28\",\n" +
                    "                \"ModelID\": \"149435\"\n" +
                    "            },\n" +
                    "            \"GB\": \"YQZ7183A\",\n" +
                    "            \"ProductionDate\": \"2014-12-11\",\n" +
                    "            \"Detail\": {\n" +
                    "                \"Chassis\": {\n" +
                    "                    \"FrontHubMaterial\": \"铝合金\",\n" +
                    "                    \"SteeringSystem\": \"机械液压助力\",\n" +
                    "                    \"FrontBrake\": \"通风盘\",\n" +
                    "                    \"Transmission\": \"6档 手自一体\",\n" +
                    "                    \"DriveHubDiameter\": \"16\",\n" +
                    "                    \"GearMode\": \"地排\",\n" +
                    "                    \"FrontTireSize\": \"205/60 R16\",\n" +
                    "                    \"SpareTire\": \"非全尺寸备胎\",\n" +
                    "                    \"DriveTireHeightAspectRatio\": \"60\",\n" +
                    "                    \"RearTireSize\": \"205/60 R16\",\n" +
                    "                    \"RearHubMaterial\": \"铝合金\",\n" +
                    "                    \"DrivingMethod\": \"前驱\",\n" +
                    "                    \"SpareTireNumSize\": \"205/60 R16\",\n" +
                    "                    \"RearBrake\": \"盘式\",\n" +
                    "                    \"RearSuspension\": \"多连杆独立悬架\",\n" +
                    "                    \"SpareTireHubMaterial\": \"\",\n" +
                    "                    \"DriveTireWidth\": \"205\",\n" +
                    "                    \"FWDMethod\": \"\",\n" +
                    "                    \"FrontSuspension\": \"麦弗逊式独立悬架\",\n" +
                    "                    \"PowerSteering\": \"液压助力\",\n" +
                    "                    \"SpareTireNum\": \"0\"\n" +
                    "                },\n" +
                    "                \"Basic\": {\n" +
                    "                    \"Warranty\": \"3年/10万公里\",\n" +
                    "                    \"BrandType\": \"中级轿车\",\n" +
                    "                    \"Emission\": \"国Ⅳ\",\n" +
                    "                    \"Country\": \"中国\",\n" +
                    "                    \"ProductionState\": \"停产\",\n" +
                    "                    \"EndDate\": \"2014-12-31\",\n" +
                    "                    \"ModelID\": \"149435\"\n" +
                    "                },\n" +
                    "                \"ElectricMotor\": {\n" +
                    "                    \"PeakTorque\": \"0.00\",\n" +
                    "                    \"ElectromotorModel\": \"\",\n" +
                    "                    \"BatteryCapacity\": \"0\",\n" +
                    "                    \"MaximumMileage\": \"0\",\n" +
                    "                    \"MaximumPower\": \"0.00\"\n" +
                    "                },\n" +
                    "                \"Driving\": {\n" +
                    "                    \"MaximumSpeed\": \"192\",\n" +
                    "                    \"MaximumGradability\": \"\",\n" +
                    "                    \"BrakingDistance\": \"0.0000\",\n" +
                    "                    \"Acceleration\": \"0.0000\"\n" +
                    "                },\n" +
                    "                \"Truck\": {\n" +
                    "                    \"LoadingSpaceLength\": \"0\",\n" +
                    "                    \"SprinNum\": \"\",\n" +
                    "                    \"LoadingSpaceWidth\": \"0\",\n" +
                    "                    \"LoadingSpaceHeight\": \"0\",\n" +
                    "                    \"LoadingSpaceType\": \"\",\n" +
                    "                    \"MaxGrossMass\": \"0\",\n" +
                    "                    \"AxleNum\": \"0\",\n" +
                    "                    \"FrontAxleLoad\": \"0\",\n" +
                    "                    \"RearAxleLoad\": \"0\"\n" +
                    "                },\n" +
                    "                \"Body\": {\n" +
                    "                    \"Trunk\": \"525\",\n" +
                    "                    \"CarBodyForm\": \"\",\n" +
                    "                    \"DragCoefficient\": \"0.0000\",\n" +
                    "                    \"RoofForm\": \"硬顶\",\n" +
                    "                    \"HoodOpening\": \"\",\n" +
                    "                    \"FrontTrack\": \"1579\",\n" +
                    "                    \"RearSuspensionLength\": \"0\",\n" +
                    "                    \"MaximumMass\": \"0\",\n" +
                    "                    \"ApproachAngle\": \"0.0000\",\n" +
                    "                    \"FrontSuspensionLength\": \"0\",\n" +
                    "                    \"Weight\": \"1395\",\n" +
                    "                    \"MinimumGroundClearance\": \"131.0000\",\n" +
                    "                    \"WheelBase\": \"2770\",\n" +
                    "                    \"HoodForm\": \"\",\n" +
                    "                    \"Length\": \"4720\",\n" +
                    "                    \"Seating\": \"5\",\n" +
                    "                    \"Doors\": \"4\",\n" +
                    "                    \"MaximumTrunk\": \"0\",\n" +
                    "                    \"Height\": \"1465\",\n" +
                    "                    \"RearTrack\": \"1589\",\n" +
                    "                    \"MinimumTurningDiameter\": \"0.0000\",\n" +
                    "                    \"Width\": \"1815\",\n" +
                    "                    \"DepartureAngle\": \"0.0000\",\n" +
                    "                    \"FuelTank\": \"62\"\n" +
                    "                },\n" +
                    "                \"Engine\": {\n" +
                    "                    \"CylinderDiameter\": \"0.0000\",\n" +
                    "                    \"PeakTorque\": \"176/4500\",\n" +
                    "                    \"Compression\": \"0.0000\",\n" +
                    "                    \"ComprehensiveFuelConsumption\": \"7.3000\",\n" +
                    "                    \"CoolingSystem\": \"水冷\",\n" +
                    "                    \"EngineDirection\": \"横向\",\n" +
                    "                    \"EngineModel\": \"G4NB\",\n" +
                    "                    \"ValveNum\": \"4\",\n" +
                    "                    \"FuelSupplyMode\": \"多点电喷\",\n" +
                    "                    \"AdmissionGear\": \"DOHC\",\n" +
                    "                    \"EngineDescription\": \"\",\n" +
                    "                    \"AirIntake\": \"自然进气\",\n" +
                    "                    \"FuelType\": \"无铅汽油92#\",\n" +
                    "                    \"EngineStroke\": \"0.0000\",\n" +
                    "                    \"MaximumPower\": \"105(143)/6200\",\n" +
                    "                    \"Exhaust\": \"1797\",\n" +
                    "                    \"CylinderNum\": \"4\",\n" +
                    "                    \"EngineImportantTechnology\": \"\",\n" +
                    "                    \"EngineManufacturer\": \"\",\n" +
                    "                    \"LitrePower\": \"58.4300\",\n" +
                    "                    \"EngineType\": \"直列\",\n" +
                    "                    \"CylinderBlock\": \"铝合金\",\n" +
                    "                    \"CylinderCover\": \"铝合金\",\n" +
                    "                    \"EnginePosition\": \"前置\"\n" +
                    "                }\n" +
                    "            },\n" +
                    "            \"Mfrs\": \"东风悦达起亚\",\n" +
                    "            \"EngineNo\": \"EW316117\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"resultCode\": 0\n" +
                    "}";*/
            CommonFinanceResult<List<FirstCarSiteVO>> financeResult1 = new CommonFinanceResult<List<FirstCarSiteVO>>();
            if (financeResult != null && !"".equals(financeResult)) {
                Type type = new TypeToken<CommonFinanceResult<List<FirstCarSiteVO>>>() {
                }.getType();
                Gson gson = new Gson();
                financeResult1 = gson.fromJson(financeResult, type);
            }

            if (financeResult1.getDatas()==null || financeResult1.getDatas().size()==0 || !financeResult1.getResultCode().equals("200"))
            {
                return ResultBean.ofError("暂无该车辆第一车网估价信息");
            }

            FirstCarSiteWebVO firstCarSiteWebVO = new FirstCarSiteWebVO();
            if (financeResult1.getDatas() != null)
            {
                FirstCarSiteVO firstCarSiteVO = financeResult1.getDatas().get(0);

                //设置值
                firstCarSiteWebVO.setBrand(firstCarSiteVO.getBrand());
                firstCarSiteWebVO.setColor(firstCarSiteVO.getColor());
                firstCarSiteWebVO.setEngineNo(firstCarSiteVO.getEngineNo());

                if (firstCarSiteVO.getDetail() != null) {
                    if (firstCarSiteVO.getDetail().getBody() != null) {
                        firstCarSiteWebVO.setCarBodyForm(firstCarSiteVO.getDetail().getBody().getCarBodyForm());
                        firstCarSiteWebVO.setDoors(firstCarSiteVO.getDetail().getBody().getDoors());
                        firstCarSiteWebVO.setSeating(firstCarSiteVO.getDetail().getBody().getSeating());
                        firstCarSiteWebVO.setWeight(firstCarSiteVO.getDetail().getBody().getWeight());
                    }

                    if (firstCarSiteVO.getDetail().getChassis() != null) {
                        firstCarSiteWebVO.setDrivingMethod(firstCarSiteVO.getDetail().getChassis().getDrivingMethod());
                    }

                    if (firstCarSiteVO.getDetail().getBasic() != null) {
                        firstCarSiteWebVO.setEmission(firstCarSiteVO.getDetail().getBasic().getEmission());
                    }

                    if (firstCarSiteVO.getDetail().getEngine() != null) {
                        firstCarSiteWebVO.setEngineModel(firstCarSiteVO.getDetail().getEngine().getEngineModel());
                        firstCarSiteWebVO.setFuelType(firstCarSiteVO.getDetail().getEngine().getFuelType());
                    }
                }
                if (firstCarSiteVO.getModel() != null) {
                    firstCarSiteWebVO.setModel_Name(firstCarSiteVO.getModel().getName());
                    /*firstCarSiteWebVO.setName(firstCarSiteVO.getModel().getName());*/
                    firstCarSiteWebVO.setProductionDate(firstCarSiteVO.getModel().getProductionYear());
                    firstCarSiteWebVO.setTransmissionType(firstCarSiteVO.getModel().getTransmissionType());
                    firstCarSiteWebVO.setVersionYear(firstCarSiteVO.getModel().getVersionYear());
                    firstCarSiteWebVO.setDisplacement(firstCarSiteVO.getModel().getDisplacement());
                }


                //保存返回Json
                SecondHandCarFirstSite secondHandCarFirstSite = new SecondHandCarFirstSite();
                secondHandCarFirstSite.setFirst_site_json(financeResult);
                secondHandCarFirstSite.setQuery_time(new Date());
                secondHandCarFirstSite.setSaleman_id(SessionUtils.getLoginUser().getId());
                secondHandCarFirstSiteMapper.insertSelective(secondHandCarFirstSite);

                //绑定订单
                //保存相关数据
                //更新绑定
                loanOrderDO.setSecond_hand_car_first_site_id(secondHandCarFirstSite.getId());
                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            }

            //设置返回值
           /* FirstCarSiteWebVO copy = BeanPlasticityUtills.copy(FirstCarSiteWebVO.class, financeResult1.getDatas().get(0));
            System.out.println("===返回值==="+copy);*/
            return ResultBean.ofSuccess(firstCarSiteWebVO);
        } else {
            //解析
            FirstCarSiteWebVO firstCarSiteWebVO = new FirstCarSiteWebVO();
            SecondHandCarFirstSite secondHandCarFirstSite = secondHandCarFirstSiteMapper.selectByPrimaryKey(loanOrderDO.getSecond_hand_car_first_site_id());
            if (secondHandCarFirstSite != null) {
                String financeResult = secondHandCarFirstSite.getFirst_site_json();
                CommonFinanceResult<List<FirstCarSiteVO>> financeResult1 = new CommonFinanceResult<List<FirstCarSiteVO>>();
                if (financeResult != null && !"".equals(financeResult)) {
                    Type type = new TypeToken<CommonFinanceResult<List<FirstCarSiteVO>>>() {
                    }.getType();
                    Gson gson = new Gson();
                    financeResult1 = gson.fromJson(financeResult, type);
                }
                if (financeResult1.getDatas() != null) {
                    FirstCarSiteVO firstCarSiteVO = financeResult1.getDatas().get(0);

                    //设置值
                    firstCarSiteWebVO.setBrand(firstCarSiteVO.getBrand());
                    firstCarSiteWebVO.setColor(firstCarSiteVO.getColor());
                    firstCarSiteWebVO.setEngineNo(firstCarSiteVO.getEngineNo());

                    if (firstCarSiteVO.getDetail() != null) {
                        if (firstCarSiteVO.getDetail().getBody() != null) {
                            firstCarSiteWebVO.setCarBodyForm(firstCarSiteVO.getDetail().getBody().getCarBodyForm());
                            firstCarSiteWebVO.setDoors(firstCarSiteVO.getDetail().getBody().getDoors());
                            firstCarSiteWebVO.setSeating(firstCarSiteVO.getDetail().getBody().getSeating());
                            firstCarSiteWebVO.setWeight(firstCarSiteVO.getDetail().getBody().getWeight());
                        }

                        if (firstCarSiteVO.getDetail().getChassis() != null) {
                            firstCarSiteWebVO.setDrivingMethod(firstCarSiteVO.getDetail().getChassis().getDrivingMethod());
                        }

                        if (firstCarSiteVO.getDetail().getBasic() != null) {
                            firstCarSiteWebVO.setEmission(firstCarSiteVO.getDetail().getBasic().getEmission());
                        }

                        if (firstCarSiteVO.getDetail().getEngine() != null) {
                            firstCarSiteWebVO.setEngineModel(firstCarSiteVO.getDetail().getEngine().getEngineModel());
                            firstCarSiteWebVO.setFuelType(firstCarSiteVO.getDetail().getEngine().getFuelType());
                        }
                    }
                    if (firstCarSiteVO.getModel() != null) {
                        firstCarSiteWebVO.setModel_Name(firstCarSiteVO.getModel().getName());
                        /*firstCarSiteWebVO.setName(firstCarSiteVO.getModel().getName());*/
                        firstCarSiteWebVO.setProductionDate(firstCarSiteVO.getModel().getProductionYear());
                        firstCarSiteWebVO.setTransmissionType(firstCarSiteVO.getModel().getTransmissionType());
                        firstCarSiteWebVO.setVersionYear(firstCarSiteVO.getModel().getVersionYear());
                        firstCarSiteWebVO.setDisplacement(firstCarSiteVO.getModel().getDisplacement());
                    }

                }

            }
            return ResultBean.ofSuccess(firstCarSiteWebVO);

        }
    }
}
