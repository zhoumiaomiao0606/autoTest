package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.VehicleInformationDO;
import com.yunche.loan.domain.param.VehicleInformationUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.VehicleInformationDOMapper;
import com.yunche.loan.service.VehicleInformationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class VehicleInformationServiceImpl implements VehicleInformationService {

    @Resource
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {
        VehicleInformationVO vehicleInformationVO = loanQueryDOMapper.selectVehicleInformation(orderId);

        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);

        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO<VehicleInformationVO> recombinationVO = new RecombinationVO<VehicleInformationVO>();
        recombinationVO.setInfo(vehicleInformationVO);
        recombinationVO.setCustomers(customers);
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalMaterialRecordByType(orderId,new Byte("19")));
        return recombinationVO;
    }

    @Override
    public void update(VehicleInformationUpdateParam param) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }

        Long foundationId  = loanOrderDO.getVehicleInformationId();//关联ID
        if(foundationId == null){
            //新增提交
            VehicleInformationDO V =  BeanPlasticityUtills.copy(VehicleInformationDO.class,param);
            vehicleInformationDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setVehicleInformationId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(vehicleInformationDOMapper.selectByPrimaryKey(foundationId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                VehicleInformationDO V= BeanPlasticityUtills.copy(VehicleInformationDO.class,param);
                V.setId(foundationId);
                vehicleInformationDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                VehicleInformationDO V= BeanPlasticityUtills.copy(VehicleInformationDO.class,param);
                V.setId(foundationId);
                vehicleInformationDOMapper.updateByPrimaryKeySelective(V);
            }
        }
    }
}
