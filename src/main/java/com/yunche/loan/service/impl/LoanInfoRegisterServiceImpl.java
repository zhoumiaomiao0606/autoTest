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
import com.yunche.loan.service.LoanFinancialPlanService;
import com.yunche.loan.service.LoanInfoRegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanInfoRegisterServiceImpl  implements LoanInfoRegisterService{

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
    private LoanFinancialPlanService loanFinancialPlanService;



    @Override
    public ResultBean detail(Long orderId) {
        Preconditions.checkNotNull(orderId,"参数有误");

        RecombinationVO recombinationVO = new RecombinationVO<>();
        //客户基本信息
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);

        //车辆详情
        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);

        //金融方案信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setCar(universalCarInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 贷款信息注册 更新
     * @param loanInfoRegisterParam
     * @return
     */
    @Override
    public ResultBean update(LoanInfoRegisterParam loanInfoRegisterParam) {
        Preconditions.checkNotNull(loanInfoRegisterParam,"参数有误");
        Preconditions.checkNotNull(loanInfoRegisterParam.getOrderId(),"业务单号未传");
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanInfoRegisterParam.getOrderId());
        Preconditions.checkNotNull(loanOrderDO,"订单信息不存在");


        Long loanCarInfoId = loanOrderDO.getLoanCarInfoId();


        LoanCarInfoDO carInfoDO = new LoanCarInfoDO();

        carInfoDO.setVehicleProperty(loanInfoRegisterParam.getVehicleProperty());
        carInfoDO.setCarType(loanInfoRegisterParam.getCarType());
        carInfoDO.setCarDetailId(loanInfoRegisterParam.getCarDetail().getId());
        carInfoDO.setCarDetailName(loanInfoRegisterParam.getCarDetail().getName());


        if(loanCarInfoId !=null){
            carInfoDO.setId(loanCarInfoId);
            int count = loanCarInfoDOMapper.updateByPrimaryKeySelective(carInfoDO);
            Preconditions.checkArgument(count>0,"更新车辆信息失败");
        }else{

            Long loanBaseInfoId = loanOrderDO.getLoanBaseInfoId();
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            Long partnerId = loanBaseInfoDO.getPartnerId();
            carInfoDO.setPartnerId(partnerId);
            int count = loanCarInfoDOMapper.insertSelective(carInfoDO);
            Preconditions.checkArgument(count>0,"插入信息失败");
            Long carinfoid = carInfoDO.getId();
            loanOrderDO.setLoanCarInfoId(carinfoid);
            int i = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(i>0,"更新业务申请数据失败");
        }

        Long vehicleInformationId = loanOrderDO.getVehicleInformationId();

        VehicleInformationDO vehicleInformationDO = new VehicleInformationDO();
        vehicleInformationDO.setId(vehicleInformationId);
        vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());
        if(vehicleInformationId!=null){
            vehicleInformationDO.setId(vehicleInformationId);
            int count = vehicleInformationDOMapper.updateByPrimaryKeySelective(vehicleInformationDO);
            Preconditions.checkArgument(count>0,"提车资料信息更新失败");
        }else{
            int count1 = vehicleInformationDOMapper.insertSelective(vehicleInformationDO);
            Long id = vehicleInformationDO.getId();
            loanOrderDO.setVehicleInformationId(id);
            int count2 = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count2>0,"提车资料信息更新失败");
        }

        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();

        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanInfoRegisterParam.getLoanFinancialPlanParam(), loanFinancialPlanDO);
        if(loanFinancialPlanId!=null){
            loanFinancialPlanDO.setId(loanFinancialPlanId);
            int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
            Preconditions.checkArgument(count>0,"插入金融方案计划失败");
        }else{
            int count = loanFinancialPlanDOMapper.insertSelective(loanFinancialPlanDO);
            Preconditions.checkArgument(count>0,"插入金融方案计划失败");
            Long id = loanFinancialPlanDO.getId();
            loanOrderDO.setLoanFinancialPlanId(id);
            int lines =loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count>0,"插入金融方案计划失败");
        }
        return ResultBean.ofSuccess(null,"保存成功");
    }
}
