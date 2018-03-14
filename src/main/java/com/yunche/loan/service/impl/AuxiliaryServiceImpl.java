package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.InstallGpsDO;
import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.GpsUpdateParam;
import com.yunche.loan.domain.param.InstallUpdateParam;
import com.yunche.loan.mapper.InstallGpsDOMapper;
import com.yunche.loan.mapper.LoanCarInfoDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.AuxiliaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class AuxiliaryServiceImpl implements AuxiliaryService {
    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private InstallGpsDOMapper installGpsDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;


    @Override
    public void commit(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }
        Long foundationId = loanOrderDO.getLoanCarInfoId();
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(foundationId);
        if(loanCarInfoDO == null){
            throw new BizException("此车辆贷款信息不存在");
        }
        loanCarInfoDO.setCarKey(new Byte("1"));
        loanCarInfoDOMapper.updateByPrimaryKey(loanCarInfoDO);
    }

    @Override
    public void install(InstallUpdateParam param) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }
        installGpsDOMapper.deleteByOrderId(Long.valueOf(param.getOrder_id()));
        //先删除-再新增-保持数据最新
        for(GpsUpdateParam obj:param.getGps_list()){
            InstallGpsDO T= BeanPlasticityUtills.copy(InstallGpsDO.class,obj);
            T.setOder_id(Long.valueOf(param.getOrder_id()));
            installGpsDOMapper.insertSelective(T);
        }
    }
}
