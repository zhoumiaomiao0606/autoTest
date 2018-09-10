package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.VehicleOutboundUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VehicleOutboundService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:44
 * @description: 车辆出库service实现类
 **/
@Service
@Transactional
public class VehicleOutboundServiceImpl implements VehicleOutboundService
{
    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private VehicleOutboundDOMapper vehicleOutboundDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private VehicleHandleDOMapper vehicleHandleDOMapper;

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

   /* @Autowired
    private LoanProcessCollectionDOMapper loanProcessCollectionDOMapper;*/

    @Autowired
    private LoanProcessInsteadPayDOMapper loanProcessInsteadPayDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;


    @Override
    public VehicleOutboundVO detail(Long orderId,Long bank_repay_imp_record_id)
    {
        VehicleOutboundVO vehicleOutboundVO =new VehicleOutboundVO();
        // TODO
        //客户主要信息
        BaseCustomerInfoVO baseCustomerInfoVO = loanQueryDOMapper.selectBaseCustomerInfoInfo(orderId);
        //车辆出库登记
        VehicleOutboundInfo vehicleOutboundInfo =new VehicleOutboundInfo();
        VehicleOutboundDOKey vehicleOutboundDOKey =new VehicleOutboundDOKey();
        vehicleOutboundDOKey.setOrderid(orderId);
        vehicleOutboundDOKey.setBankRepayImpRecordId(bank_repay_imp_record_id);
        VehicleOutboundDO vehicleOutboundDO = vehicleOutboundDOMapper.selectByPrimaryKey(vehicleOutboundDOKey);
        //根据区id查询省市id
       if(vehicleOutboundDO !=null )
       {
           if(vehicleOutboundDO.getAddress()!=null && !"".equals(vehicleOutboundDO.getAddress().trim()))
           {
               Long countyId = Long.valueOf(vehicleOutboundDO.getAddress());
               BaseAreaDO cityAreaDO = baseAreaDOMapper.selectByPrimaryKey(countyId, VALID_STATUS);
               vehicleOutboundInfo.setCountyId(countyId);
               if (cityAreaDO != null && cityAreaDO.getParentAreaId() != null) {
                   vehicleOutboundInfo.setCityId(cityAreaDO.getParentAreaId());
                   BaseAreaDO provenceAreaDO = baseAreaDOMapper.selectByPrimaryKey(cityAreaDO.getParentAreaId(), VALID_STATUS);
                   vehicleOutboundInfo.setProvenceId(provenceAreaDO.getParentAreaId());
               }
           }

           BeanUtils.copyProperties(vehicleOutboundDO, vehicleOutboundInfo);

       }



        //贷款金额
        BigDecimal loan_amount = loanFinancialPlanDOMapper.selectLoanAmount(orderId);
        vehicleOutboundInfo.setLoan_amount(loan_amount);
        // 代偿金额 ---需要等流程走通
        //先到loan_process_collection取到流程实例id --loan_process_collection
//        LoanProcessCollectionDO loanProcessCollectionDO = loanProcessCollectionDOMapper.selectByPrimaryOrderIdAndBankRepayImpRecordId(orderId, bank_repay_imp_record_id);
        //根据流程实例id和订单id取到代偿单id
     /*   loanProcessInsteadPayDOMapper.selectByOrderIdAndInsteadPayOrderId(loanProcessCollectionDO.getOrderId(),loanProcessCollectionDO.getProcessInstId())*/
        LoanProcessInsteadPayDO loanProcessInsteadPayDO = loanProcessInsteadPayDOMapper.selectByOrderIdAndInsteadPayOrderIdReddlyReview(orderId, bank_repay_imp_record_id);
        if (loanProcessInsteadPayDO !=null)
        {
            LoanApplyCompensationDO loanApplyCompensationDO = loanApplyCompensationDOMapper.selectByOrderIdAndBankRepayImpRecordId(orderId, bank_repay_imp_record_id);
            if (loanApplyCompensationDO !=null)
            {
                vehicleOutboundInfo.setCompensation_amount_sum(loanApplyCompensationDO.getCompensationAmount());
            }
        }




        // 清收成本
        VehicleHandleDO vehicleHandleDO = vehicleHandleDOMapper.selectByPrimaryKey(new VehicleHandleDOKey(orderId, bank_repay_imp_record_id));
        if(vehicleHandleDO !=null)
        {
            vehicleOutboundInfo.setFinal_costs(vehicleHandleDO.getFinalCosts());
        }
        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        vehicleInfoVO.setApply_license_plate_area(tmpApplyLicensePlateArea);



        //贷款业务详细信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        //本业务操作日志
        vehicleOutboundVO.setBaseCustomerInfoVO(baseCustomerInfoVO);
        if (vehicleOutboundInfo !=null)
        {
            vehicleOutboundVO.setVehicleOutboundInfo(vehicleOutboundInfo);
        }

        if (vehicleInfoVO !=null)
        {
            vehicleOutboundVO.setVehicleInfoVO(vehicleInfoVO);
        }


        vehicleOutboundVO.setCustomers(customers);

        return vehicleOutboundVO;
    }

    @Override
    public ResultBean<Void> update(VehicleOutboundDO param)
    {
        Preconditions.checkNotNull(param.getOrderid(), "订单号不能为空");
        Preconditions.checkNotNull(param.getBankRepayImpRecordId(), "版本号不能为空");

        VehicleOutboundDOKey vehicleOutboundDOKey =new VehicleOutboundDOKey();
        vehicleOutboundDOKey.setOrderid(param.getOrderid());
        vehicleOutboundDOKey.setBankRepayImpRecordId(param.getBankRepayImpRecordId());

        VehicleOutboundDO  existDO = vehicleOutboundDOMapper.selectByPrimaryKey(vehicleOutboundDOKey);

        VehicleOutboundDO vehicleOutboundDO =new VehicleOutboundDO();
        BeanUtils.copyProperties(param, vehicleOutboundDO);
        if (null == existDO) {
            // create
            int count = vehicleOutboundDOMapper.insertSelective(vehicleOutboundDO);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            int count = vehicleOutboundDOMapper.updateByPrimaryKeySelective(vehicleOutboundDO);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:
    */
    @Override
    public VehicleOutboundInfo vehicleOutbound(Long orderId, Long bank_repay_imp_record_id)
    {
        //车辆出库登记
        VehicleOutboundInfo vehicleOutboundInfo =new VehicleOutboundInfo();
        VehicleOutboundDOKey vehicleOutboundDOKey =new VehicleOutboundDOKey();
        vehicleOutboundDOKey.setOrderid(orderId);
        vehicleOutboundDOKey.setBankRepayImpRecordId(bank_repay_imp_record_id);
        VehicleOutboundDO vehicleOutboundDO = vehicleOutboundDOMapper.selectByPrimaryKey(vehicleOutboundDOKey);
        //根据区id查询省市id
        if(vehicleOutboundDO !=null )
        {
            StringBuilder stringBuilder =new StringBuilder();
            if(vehicleOutboundDO.getAddress()!=null && !"".equals(vehicleOutboundDO.getAddress().trim()))
            {
                Long countyId = Long.valueOf(vehicleOutboundDO.getAddress());
                BaseAreaDO cityAreaDO = baseAreaDOMapper.selectByPrimaryKey(countyId, VALID_STATUS);
                vehicleOutboundInfo.setCountyId(countyId);
                if (cityAreaDO != null && cityAreaDO.getParentAreaId() != null) {
                    vehicleOutboundInfo.setCityId(cityAreaDO.getParentAreaId());
                    BaseAreaDO provenceAreaDO = baseAreaDOMapper.selectByPrimaryKey(cityAreaDO.getParentAreaId(), VALID_STATUS);
                    vehicleOutboundInfo.setProvenceId(provenceAreaDO.getParentAreaId());
                    if(provenceAreaDO.getParentAreaName() !=null)
                    {
                        stringBuilder.append(provenceAreaDO.getParentAreaName());
                    }
                    stringBuilder.append(provenceAreaDO.getAreaName());
                }
                stringBuilder.append(cityAreaDO.getAreaName());
            }

            BeanUtils.copyProperties(vehicleOutboundDO, vehicleOutboundInfo);
            vehicleOutboundInfo.setAddress(stringBuilder.toString());

        }
        return vehicleOutboundInfo;
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:
    */
    @Override
    public BaseCustomerInfoVO customer(Long orderId,Long bank_repay_imp_record_id)
    {
        //客户主要信息
        BaseCustomerInfoVO baseCustomerInfoVO = loanQueryDOMapper.selectBaseCustomerInfoInfo(orderId);
        LoanApplyCompensationDO loanApplyCompensationDO = loanApplyCompensationDOMapper.selectByOrderIdAndBankRepayImpRecordId(orderId,bank_repay_imp_record_id);
        //贷款金额
        BigDecimal loan_amount = loanFinancialPlanDOMapper.selectLoanAmount(orderId);
        if(loanApplyCompensationDO !=null)
        {
            baseCustomerInfoVO.setLoanBanlance(loan_amount);
            baseCustomerInfoVO.setCompensationAmount(loanApplyCompensationDO.getCompensationAmount());
            baseCustomerInfoVO.setCurrArrears(loanApplyCompensationDO.getCurrArrears());
        }

        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        if(vehicleInfoVO != null)
        {
            baseCustomerInfoVO.setLicense_plate_number(vehicleInfoVO.getLicense_plate_number());
            baseCustomerInfoVO.setCar_name(vehicleInfoVO.getCar_name());
        }


        // 清收成本
        VehicleHandleDO vehicleHandleDO = vehicleHandleDOMapper.selectByPrimaryKey(new VehicleHandleDOKey(orderId, bank_repay_imp_record_id));
        if(vehicleHandleDO !=null)
        {
            baseCustomerInfoVO.setFinalCosts(vehicleHandleDO.getFinalCosts());
        }

        return baseCustomerInfoVO;
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:
    */
    @Override
    public VehicleInfoVO vehicleInfo(Long orderId)
    {
        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        return vehicleInfoVO;
    }
}
