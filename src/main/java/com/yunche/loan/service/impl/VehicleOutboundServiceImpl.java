package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.VehicleHandleDO;
import com.yunche.loan.domain.entity.VehicleOutboundDO;
import com.yunche.loan.domain.entity.VehicleOutboundDOKey;
import com.yunche.loan.domain.param.VehicleOutboundUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.VehicleHandleDOMapper;
import com.yunche.loan.mapper.VehicleOutboundDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VehicleOutboundService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    @Override
    public VehicleOutboundVO detail(Long orderId,Long bank_repay_imp_record_id)
    {
        VehicleOutboundVO vehicleOutboundVO =new VehicleOutboundVO();
        // TODO
        //客户主要信息
        BaseCustomerInfoVO baseCustomerInfoVO = loanQueryDOMapper.selectBaseCustomerInfoInfo(orderId);
        //车辆出库登记
        VehicleOutboundDOKey vehicleOutboundDOKey =new VehicleOutboundDOKey();
        vehicleOutboundDOKey.setOrderid(orderId);
        vehicleOutboundDOKey.setBankRepayImpRecordId(bank_repay_imp_record_id);
        VehicleOutboundDO vehicleOutboundDO = vehicleOutboundDOMapper.selectByPrimaryKey(vehicleOutboundDOKey);
        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        //贷款业务详细信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        //本业务操作日志
        vehicleOutboundVO.setBaseCustomerInfoVO(baseCustomerInfoVO);
        vehicleOutboundVO.setVehicleOutboundDO(vehicleOutboundDO);
        vehicleOutboundVO.setVehicleInfoVO(vehicleInfoVO);
        vehicleOutboundVO.setCustomers(customers);

        return vehicleOutboundVO;
    }

    @Override
    public ResultBean<Void> update(VehicleOutboundDO param)
    {
        Preconditions.checkNotNull(param.getOrderid(), "订单号不能为空");

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
}
