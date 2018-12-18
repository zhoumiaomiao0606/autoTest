package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.LoanInfoRegisterParam;
import com.yunche.loan.domain.vo.FinancialSchemeVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCarInfoVO;
import com.yunche.loan.domain.vo.UniversalInfoVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.LoanInfoRegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class LoanInfoRegisterServiceImpl implements LoanInfoRegisterService {

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private SecondHandCarEvaluateDOMapper secondHandCarEvaluateDOMapper;


    @Override
    public ResultBean detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "参数有误");

        //客户基本信息
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        //车辆详情
        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);

        //判断估价类型
        if (universalCarInfoVO!=null && universalCarInfoVO.getCar_type()!=null && !"".equals(universalCarInfoVO.getCar_type()) && Byte.valueOf(universalCarInfoVO.getCar_type())==1)
        {
            if (universalCarInfoVO.getEvaluation_type()!=null && universalCarInfoVO.getEvaluation_type() ==2)
            {
                universalCarInfoVO.setVin(universalCarInfoVO.getVehicle_vehicle_identification_number());
            }

        }

        //金融方案信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

        RecombinationVO recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setCar(universalCarInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);

        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 贷款信息注册 更新
     *
     * @param loanInfoRegisterParam
     * @return
     */
    @Override
    @Transactional
    public ResultBean update(LoanInfoRegisterParam loanInfoRegisterParam) {
        Preconditions.checkNotNull(loanInfoRegisterParam, "参数有误");
        Preconditions.checkNotNull(loanInfoRegisterParam.getOrderId(), "业务单号未传");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanInfoRegisterParam.getOrderId());
        Preconditions.checkNotNull(loanOrderDO, "订单信息不存在");
        VehicleInformationDO vehicleInformationDO = new VehicleInformationDO();
        //更新vin码绑定
            if (loanInfoRegisterParam.getCarType() ==0 )
            {
                loanOrderDO.setSecond_hand_car_evaluate_id(null);
                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
                vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());
            }else {

                if (loanInfoRegisterParam.getEvaluationType()==2)//手工评估
                {
                    vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());

                    //更新vin码---车辆行驶证号码
                    vehicleInformationDO.setVehicle_identification_number(loanInfoRegisterParam.getVin());

                    loanOrderDO.setSecond_hand_car_evaluate_id(null);
                    loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

                }else if(loanInfoRegisterParam.getEvaluationType()==1)//在线评估
                {
                    SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanInfoRegisterParam.getSecond_hand_car_evaluate_id());

                    // #车牌号码 #车辆类型（小型轿车）  #所有人名称  #发动机号码  #注册日期   #车型颜色
                    //vehicleInformationDO.setLicense_plate_number(secondHandCarEvaluateDO.getPlate_num());
                    vehicleInformationDO.setCar_category(secondHandCarEvaluateDO.getVehicle_type());
                    //vehicleInformationDO.setNow_driving_license_owner(secondHandCarEvaluateDO.getOwner());
                    vehicleInformationDO.setEngine_number(secondHandCarEvaluateDO.getEngine_num());
                    vehicleInformationDO.setRegister_date(secondHandCarEvaluateDO.getRegister_date());
                    /*vehicleInformationDO.setColor(secondHandCarEvaluateDO.getStyle_color());*/
                    vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());
                    vehicleInformationDO.setVehicle_identification_number(secondHandCarEvaluateDO.getVin());
                    loanOrderDO.setSecond_hand_car_evaluate_id(loanInfoRegisterParam.getSecond_hand_car_evaluate_id());
                }else{
                    throw new BizException("估价类型有误");
                }

            }

            //更新
        loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

        // loanCarInfo
        Long loanCarInfoId = loanOrderDO.getLoanCarInfoId();

        LoanCarInfoDO carInfoDO = new LoanCarInfoDO();
        carInfoDO.setVehicleProperty(loanInfoRegisterParam.getVehicleProperty());
        carInfoDO.setCarType(loanInfoRegisterParam.getCarType());
        carInfoDO.setEvaluationType(loanInfoRegisterParam.getEvaluationType());
        carInfoDO.setCarDetailId(loanInfoRegisterParam.getCarDetail().getId());
        carInfoDO.setCarDetailName(loanInfoRegisterParam.getCarDetail().getName());

        if (null != loanCarInfoId) {

            // update
            carInfoDO.setId(loanCarInfoId);
            carInfoDO.setGmtModify(new Date());
            int count = loanCarInfoDOMapper.updateByPrimaryKeySelective(carInfoDO);
            Preconditions.checkArgument(count > 0, "保存车辆信息失败");

        } else {

            // insert
            Long loanBaseInfoId = loanOrderDO.getLoanBaseInfoId();
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            Long partnerId = loanBaseInfoDO.getPartnerId();
            carInfoDO.setPartnerId(partnerId);
            carInfoDO.setGmtCreate(new Date());
            carInfoDO.setGmtModify(new Date());

            int count = loanCarInfoDOMapper.insertSelective(carInfoDO);
            Preconditions.checkArgument(count > 0, "保存车辆信息失败");

            loanOrderDO.setLoanCarInfoId(carInfoDO.getId());
            loanOrderDO.setGmtModify(new Date());
            int count2 = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count2 > 0, "保存车辆信息失败");
        }


        // vehicleInformation
        Long vehicleInformationId = loanOrderDO.getVehicleInformationId();

        vehicleInformationDO.setId(vehicleInformationId);


        if (null != vehicleInformationId) {

            // update
            vehicleInformationDO.setId(vehicleInformationId);
            int count = vehicleInformationDOMapper.updateByPrimaryKeySelective(vehicleInformationDO);
            Preconditions.checkArgument(count > 0, "保存提车资料失败");

        } else {

            // insert
            int count1 = vehicleInformationDOMapper.insertSelective(vehicleInformationDO);
            Preconditions.checkArgument(count1 > 0, "保存提车资料失败");

            loanOrderDO.setVehicleInformationId(vehicleInformationDO.getId());
            loanOrderDO.setGmtModify(new Date());
            int count2 = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count2 > 0, "保存提车资料失败");
        }


        // loanFinancialPlan
        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();

        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanInfoRegisterParam.getLoanFinancialPlanParam(), loanFinancialPlanDO);

        if (null != loanFinancialPlanId) {

            // update
            loanFinancialPlanDO.setId(loanFinancialPlanId);
            loanFinancialPlanDO.setGmtModify(new Date());
            int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
            Preconditions.checkArgument(count > 0, "插入金融方案计划失败");

        } else {

            // insert
            loanFinancialPlanDO.setGmtCreate(new Date());
            loanFinancialPlanDO.setGmtModify(new Date());
            int count = loanFinancialPlanDOMapper.insertSelective(loanFinancialPlanDO);
            Preconditions.checkArgument(count > 0, "插入金融方案计划失败");

            loanOrderDO.setLoanFinancialPlanId(loanFinancialPlanDO.getId());
            loanOrderDO.setGmtModify(new Date());
            int count2 = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count2 > 0, "插入金融方案计划失败");
        }
        if(loanInfoRegisterParam.getWorkCompanyName() !=null){
            Long cusId = loanOrderDO.getLoanCustomerId();
            LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
            loanCustomerDO.setId(cusId);
            if(loanInfoRegisterParam.getMonthIncome() !=null && !"".equals(loanInfoRegisterParam.getMonthIncome())){
                loanCustomerDO.setMonthIncome(new BigDecimal(loanInfoRegisterParam.getMonthIncome()));
            }
            loanCustomerDO.setIncomeCertificateCompanyName(loanInfoRegisterParam.getWorkCompanyName());
            loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }
}
