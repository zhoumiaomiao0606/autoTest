package com.yunche.loan.service.impl;

import com.google.common.collect.Lists;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.InsuranceInfoDO;
import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.InsuranceRelevanceUpdateParam;
import com.yunche.loan.domain.param.InsuranceUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.InsuranceInfoDOMapper;
import com.yunche.loan.mapper.InsuranceRelevanceDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.InsuranceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class InsuranceServiceImpl implements InsuranceService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Resource
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {
        List<InsuranceCustomerVO> insuranceCustomerVOList = loanQueryDOMapper.selectInsuranceCustomer(orderId);
        for(InsuranceCustomerVO obj:insuranceCustomerVOList){
            if(obj!=null) {
                if (obj.getInsurance_info_id() != null) {
                    List<InsuranceRelevanceVO> insurance_relevance_list = loanQueryDOMapper.selectInsuranceRelevance(Long.valueOf(obj.getInsurance_info_id()));
                    obj.setInsurance_relevance_list(insurance_relevance_list);
                }
            }
        }
        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
        RecombinationVO<List<InsuranceCustomerVO>> recombinationVO = new RecombinationVO<List<InsuranceCustomerVO>>();
        List<InsuranceInfoDO> insuranceInfoDOS = insuranceInfoDOMapper.listByOrderId(orderId);

        List<UniversalInsuranceVO> insuranceDetail = Lists.newArrayList();
        insuranceInfoDOS.stream().forEach(e->{
            UniversalInsuranceVO universalInsuranceVO = new UniversalInsuranceVO();
            Byte year = e.getInsurance_year();
            List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.listByInsuranceInfoId(orderId);

            universalInsuranceVO.setInsuranceYear(year);
            universalInsuranceVO.setInsuranceRele(insuranceRelevanceDOS);
            insuranceDetail.add(universalInsuranceVO);
        });
        recombinationVO.setCar(universalCarInfoVO);//车辆信息
        recombinationVO.setInfo(insuranceCustomerVOList);
        recombinationVO.setInsuranceDetail(insuranceDetail);
        return recombinationVO;
    }

    @Override
    public RecombinationVO query(Long orderId) {
        InsuranceCustomerVO insuranceCustomerVO = loanQueryDOMapper.selectInsuranceCustomerNormalizeInsuranceYear(orderId);
        if(insuranceCustomerVO!=null){
            if(insuranceCustomerVO.getInsurance_info_id()!=null){
                List<InsuranceRelevanceVO> insurance_relevance_list = loanQueryDOMapper.selectInsuranceRelevance(Long.valueOf(insuranceCustomerVO.getInsurance_info_id()));
                insuranceCustomerVO.setInsurance_relevance_list(insurance_relevance_list);
            }
         }
        RecombinationVO<InsuranceCustomerVO> recombinationVO = new RecombinationVO<InsuranceCustomerVO>();
        recombinationVO.setInfo(insuranceCustomerVO);
        return recombinationVO;
    }

    @Override
    public void update(InsuranceUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }
        //新保录入接口只能查1-续保后期在做
        InsuranceInfoDO insuranceInfoDO = insuranceInfoDOMapper.selectByInsuranceYear(Long.valueOf(param.getOrder_id()),new Byte("1"));
        if(insuranceInfoDO == null){
            //新增所有关联数据
            InsuranceInfoDO V= BeanPlasticityUtills.copy(InsuranceInfoDO.class,param);
            V.setOrder_id(Long.valueOf(param.getOrder_id()));
            V.setIssue_bills_date(new Date());
            V.setInsurance_year(new Byte("1"));
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
            //开始更新保险公司关联表
            //先删除保险公司关联数据在进行新增-保持保险公司的关联信息是最新的
            insuranceRelevanceDOMapper.deleteByInsuranceInfoId(insuranceInfoDO.getId());
            for(InsuranceRelevanceUpdateParam obj:param.getInsurance_relevance_list()){
                InsuranceRelevanceDO T= BeanPlasticityUtills.copy(InsuranceRelevanceDO.class,obj);
                T.setInsurance_info_id(insuranceInfoDO.getId());
                insuranceRelevanceDOMapper.insertSelective(T);
            }
        }

    }
}
