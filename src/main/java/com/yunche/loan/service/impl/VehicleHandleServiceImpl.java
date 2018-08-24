package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.param.VehicleHandleUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VehicleHandleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

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

    @Resource
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private VisitDoorDOMapper visitDoorDOMapper;


    @Override
    public VehicleHandleVO detail(Long orderId,Long bankRepayImpRecordId)
    {
        VehicleHandleVO vehicleHandleVO =new VehicleHandleVO();
        // TODO
        //客户主要信息
        BaseCustomerInfoVO baseCustomerInfoVO = loanQueryDOMapper.selectBaseCustomerInfoInfo(orderId);
        //车辆处理登记
        VehicleHandleDO vehicleHandleDO = vehicleHandleDOMapper.selectByPrimaryKey(new VehicleHandleDOKey(orderId,bankRepayImpRecordId));
        //根据区id查询省市id
        if(vehicleHandleDO !=null )
        {
            if(vehicleHandleDO.getVehicleInboundAddress()!=null && !"".equals(vehicleHandleDO.getVehicleInboundAddress().trim()))
            {
            Long countyId=Long.valueOf(vehicleHandleDO.getVehicleInboundAddress());
            BaseAreaDO cityAreaDO = baseAreaDOMapper.selectByPrimaryKey(countyId, VALID_STATUS);
            vehicleHandleDO.setCountyId(countyId);
            vehicleHandleDO.setCountyName(cityAreaDO.getAreaName());
            vehicleHandleDO.setCityName(cityAreaDO.getParentAreaName());
            if(cityAreaDO !=null && cityAreaDO.getParentAreaId()!=null)
            {
                vehicleHandleDO.setCityId(cityAreaDO.getParentAreaId());
                BaseAreaDO provenceAreaDO = baseAreaDOMapper.selectByPrimaryKey(cityAreaDO.getParentAreaId(), VALID_STATUS);
                vehicleHandleDO.setProvenceId(provenceAreaDO.getParentAreaId());
                vehicleHandleDO.setProvenceName(provenceAreaDO.getAreaName());
            }

            }

            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
            Long customerId = loanOrderDO.getLoanCustomerId();
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(customerId)
                    .stream().filter(universalCustomerFileVO ->  universalCustomerFileVO.getType().equals("93"))
                    .collect(Collectors.toList());


            vehicleHandleDO.setFiles(files);

        }else
            {
            vehicleHandleDO =new VehicleHandleDO();
            }

        VisitDoorDO visitDoorDO = visitDoorDOMapper.selectByOrderIdAndRecordId(orderId, bankRepayImpRecordId);
        if (visitDoorDO !=null)
        {
            vehicleHandleDO.setHanddlePerson(visitDoorDO.getVisitPeopleName());
        }
        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        //贷款业务详细信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files1 = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files1);
        }
        //本业务操作日志
        vehicleHandleVO.setVehicleHandleDO(vehicleHandleDO);
        vehicleHandleVO.setBaseCustomerInfoVO(baseCustomerInfoVO);
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

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrderid()));

        Long customerId = loanOrderDO.getLoanCustomerId();

        if (customerId != null && param.getFiles() != null && !param.getFiles().isEmpty()) {
            for (UniversalFileParam universalFileParam : param.getFiles()) {
                List<LoanFileDO> uploadList = loanFileDOMapper.listByCustomerIdAndType(customerId, new Byte(universalFileParam.getType()), null);
                for (LoanFileDO loanFileDO : uploadList) {
                    loanFileDOMapper.deleteByPrimaryKey(loanFileDO.getId());
                }
                LoanFileDO loanFileDO = new LoanFileDO();
                loanFileDO.setCustomerId(customerId);
                loanFileDO.setPath(JSON.toJSONString(universalFileParam.getUrls()));
                loanFileDO.setType(new Byte(universalFileParam.getType()));
                loanFileDO.setUploadType(new Byte("1"));
                loanFileDO.setGmtCreate(new Date());
                loanFileDO.setGmtModify(new Date());
                loanFileDO.setStatus(new Byte("0"));
                loanFileDOMapper.insertSelective(loanFileDO);
            }
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
    public VehicleHandleDO vehicleHandle(Long orderId, Long bank_repay_imp_record_id)
    {
        //车辆处理登记
        VehicleHandleDO vehicleHandleDO = vehicleHandleDOMapper.selectByPrimaryKey(new VehicleHandleDOKey(orderId,bank_repay_imp_record_id));
        //根据区id查询省市id
        if(vehicleHandleDO !=null )
        {
            StringBuilder stringBuilder =new StringBuilder();
            //
            if(vehicleHandleDO.getVehicleInboundAddress()!=null && !"".equals(vehicleHandleDO.getVehicleInboundAddress().trim()))
            {
                Long countyId = Long.valueOf(vehicleHandleDO.getVehicleInboundAddress());
                BaseAreaDO cityAreaDO = baseAreaDOMapper.selectByPrimaryKey(countyId, VALID_STATUS);
                vehicleHandleDO.setCountyId(countyId);
                if (cityAreaDO != null && cityAreaDO.getParentAreaId() != null) {
                    vehicleHandleDO.setCityId(cityAreaDO.getParentAreaId());
                    BaseAreaDO provenceAreaDO = baseAreaDOMapper.selectByPrimaryKey(cityAreaDO.getParentAreaId(), VALID_STATUS);
                    vehicleHandleDO.setProvenceId(provenceAreaDO.getParentAreaId());
                    if(provenceAreaDO.getParentAreaName() !=null)
                    {
                        stringBuilder.append(provenceAreaDO.getParentAreaName());
                    }
                    stringBuilder.append(provenceAreaDO.getAreaName());
                }
                stringBuilder.append(cityAreaDO.getAreaName());
            }
            vehicleHandleDO.setVehicleInboundAddress(stringBuilder.toString());
            //

            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
            Long customerId = loanOrderDO.getLoanCustomerId();
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(customerId)
                    .stream().filter(universalCustomerFileVO ->  universalCustomerFileVO.getType().equals("93"))
                    .collect(Collectors.toList());


            vehicleHandleDO.setFiles(files);


        }else
        {
            vehicleHandleDO =new VehicleHandleDO();
        }

        VisitDoorDO visitDoorDO = visitDoorDOMapper.selectByOrderIdAndRecordId(orderId, bank_repay_imp_record_id);
        if (visitDoorDO !=null)
        {
            vehicleHandleDO.setHanddlePerson(visitDoorDO.getVisitPeopleName());
        }
        return vehicleHandleDO;
    }
}
