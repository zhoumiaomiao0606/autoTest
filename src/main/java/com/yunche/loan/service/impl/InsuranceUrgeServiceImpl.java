package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.InsuranceInfoDO;
import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.query.InsuranceListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.InsuranceUrgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 催保
 */
@Service
public class InsuranceUrgeServiceImpl implements InsuranceUrgeService{

    @Autowired
    private BankRecordQueryDOMapper bankRecordQueryDOMapper;


    @Autowired
    private InsuranceDistributeRecordDOMapper insuranceDistributeRecordDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Autowired
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Override
    public List list(InsuranceListQuery insuranceListQuery) {

        List<InsuranceUrgeVO> urgeVOList = bankRecordQueryDOMapper.selectInsuranceUrgeTaskList(insuranceListQuery);

        return urgeVOList;
    }


    @Override
    public ResultBean detail(Long orderId) {
        {

            RecombinationVO recombinationVO = new RecombinationVO<>();
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);

            Preconditions.checkNotNull(loanOrderDO,"订单不存在");

            UniversalCustomerDetailVO universalCustomerDetailVO = loanQueryDOMapper.selectUniversalCustomerDetail(orderId, loanOrderDO.getLoanCustomerId());

            FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

            UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);


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

            List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
            for (UniversalCustomerVO universalCustomerVO : customers) {
                List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
                universalCustomerVO.setFiles(files);
            }
            recombinationVO.setInfo(universalCustomerDetailVO);
            recombinationVO.setFinancial(financialSchemeVO);
            recombinationVO.setCar(universalCarInfoVO);
            recombinationVO.setInsuranceDetail(insuranceDetail);
            recombinationVO.setCustomers(customers);
            return ResultBean.ofSuccess(recombinationVO);
        }
    }
}
