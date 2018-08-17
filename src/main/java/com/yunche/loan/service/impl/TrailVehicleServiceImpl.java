package com.yunche.loan.service.impl;


import com.yunche.loan.domain.param.TrailVehicleUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.TrailVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 09:39
 * @description: 上门拖车service处理类
 **/
@Service
@Transactional
public class TrailVehicleServiceImpl implements TrailVehicleService
{
    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;
    @Override
    public TrailVehicleDetailVO detail(Long orderId)
    {
        // TODO
        //客户主要信息
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        //逾期代偿概况

        //金融方案
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);
        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        //贷款业务详细信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        //本业务操作日志--流程接口
        return null;
    }

    @Override
    public void update(TrailVehicleUpdateParam param) {

    }
}
