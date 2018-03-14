package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.InsuranceInfoDO;
import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.InsuranceRelevanceUpdateParam;
import com.yunche.loan.domain.param.InsuranceUpdateParam;
import com.yunche.loan.mapper.InsuranceInfoDOMapper;
import com.yunche.loan.mapper.InsuranceRelevanceDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.InsuranceService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InsuranceServiceImpl implements InsuranceService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Resource
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Override
    public Map detail(Long orderId) {
        return null;
    }

    @Override
    public void update(InsuranceUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }

        InsuranceInfoDO insuranceInfoDO = insuranceInfoDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));
        if(insuranceInfoDO == null){
            //新增所有关联数据
            InsuranceInfoDO V= BeanPlasticityUtills.copy(InsuranceInfoDO.class,param);
            V.setOrder_id(Long.valueOf(param.getOrder_id()));
            insuranceInfoDOMapper.insertSelective(V);
            //开始新增保险公司关联表
            //先删除保险公司关联数据在进行新增-保持保险公司的关联信息是最新的
            insuranceRelevanceDOMapper.deleteByInsuranceInfoId(V.getId());
            for(InsuranceRelevanceUpdateParam obj:param.getInsurance_relevance_list()){
                InsuranceRelevanceDO T= BeanPlasticityUtills.copy(InsuranceRelevanceDO.class,obj);
                T.setInsurance_info_id(V.getId());
                insuranceRelevanceDOMapper.insertSelective(T);
            }
        }else {
            //代表存在
            //进行更新
            InsuranceInfoDO V= BeanPlasticityUtills.copy(InsuranceInfoDO.class,param);
            V.setId(insuranceInfoDO.getId());
            V.setOrder_id(insuranceInfoDO.getOrder_id());
            insuranceInfoDOMapper.updateByPrimaryKeySelective(V);
            //开始更新保险公司关联表
            //先删除保险公司关联数据在进行新增-保持保险公司的关联信息是最新的
            insuranceRelevanceDOMapper.deleteByInsuranceInfoId(V.getId());
            for(InsuranceRelevanceUpdateParam obj:param.getInsurance_relevance_list()){
                InsuranceRelevanceDO T= BeanPlasticityUtills.copy(InsuranceRelevanceDO.class,obj);
                T.setInsurance_info_id(V.getId());
                insuranceRelevanceDOMapper.insertSelective(T);
            }
        }

    }
}
