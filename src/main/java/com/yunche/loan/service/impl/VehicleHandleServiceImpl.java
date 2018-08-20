package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.VehicleHandleDO;
import com.yunche.loan.domain.entity.VehicleHandleDOKey;
import com.yunche.loan.domain.param.VehicleHandleUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.VehicleHandleDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VehicleHandleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:39
 * @description: 车辆处理service实现类
 **/
@Service
@Transactional
public class VehicleHandleServiceImpl implements VehicleHandleService
{
    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private VehicleHandleDOMapper vehicleHandleDOMapper;


    @Override
    public VehicleHandleVO detail(Long orderId,Long bankRepayImpRecordId)
    {
        VehicleHandleVO vehicleHandleVO =new VehicleHandleVO();
        // TODO
        //客户主要信息
        BaseCustomerInfoVO baseCustomerInfoVO = loanQueryDOMapper.selectBaseCustomerInfoInfo(orderId);
        //车辆处理登记
        VehicleHandleDO vehicleHandleDO = vehicleHandleDOMapper.selectByPrimaryKey(new VehicleHandleDOKey(orderId,bankRepayImpRecordId));

        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        //贷款业务详细信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        //本业务操作日志
        vehicleHandleVO.setBaseCustomerInfoVO(baseCustomerInfoVO);
        vehicleHandleVO.setVehicleHandleDO(vehicleHandleDO);
        vehicleHandleVO.setVehicleInfoVO(vehicleInfoVO);
        vehicleHandleVO.setCustomers(customers);
        return vehicleHandleVO;
    }

    @Override
    public ResultBean<Void>  update(VehicleHandleUpdateParam param)
    {
        Preconditions.checkNotNull(param.getOrderid(), "订单号不能为空");
        Preconditions.checkNotNull(param.getBankRepayImpRecordId(), "版本号不能为空");

        VehicleHandleDO  existDO = vehicleHandleDOMapper.selectByPrimaryKey(new VehicleHandleDOKey(param.getOrderid(),param.getBankRepayImpRecordId()));

        VehicleHandleDO vehicleHandleDO =new VehicleHandleDO();
        BeanUtils.copyProperties(param, vehicleHandleDO);
        if (null == existDO) {
            // create
            int count = vehicleHandleDOMapper.insertSelective(vehicleHandleDO);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            int count = vehicleHandleDOMapper.updateByPrimaryKeySelective(vehicleHandleDO);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");

    }
}
