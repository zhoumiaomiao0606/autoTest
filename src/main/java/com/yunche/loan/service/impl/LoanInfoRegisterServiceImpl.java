package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
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


    @Override
    public ResultBean detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "参数有误");

        //客户基本信息
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        //车辆详情
        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
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


        // loanCarInfo
        Long loanCarInfoId = loanOrderDO.getLoanCarInfoId();

        LoanCarInfoDO carInfoDO = new LoanCarInfoDO();
        carInfoDO.setVehicleProperty(loanInfoRegisterParam.getVehicleProperty());
        carInfoDO.setCarType(loanInfoRegisterParam.getCarType());
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

        VehicleInformationDO vehicleInformationDO = new VehicleInformationDO();
        vehicleInformationDO.setId(vehicleInformationId);
        vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());

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

        // 客户信息
        loanCustomerService.update(loanInfoRegisterParam.getLoanCustomerDO());

        return ResultBean.ofSuccess(null, "保存成功");
    }
}
