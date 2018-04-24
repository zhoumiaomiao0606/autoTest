package com.yunche.loan.service.impl;


import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanTempDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanTempHisDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.FinancialSchemeModifyUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.FinancialSchemeService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class FinancialSchemeServiceImpl implements FinancialSchemeService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanFinancialPlanTempDOMapper loanFinancialPlanTempDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanFinancialPlanTempHisDOMapper loanFinancialPlanTempHisDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);
        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        RecombinationVO<FinancialSchemeVO> recombinationVO = new RecombinationVO<FinancialSchemeVO>();
        recombinationVO.setInfo(financialSchemeVO);
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    public RecombinationVO verifyDetail(Long orderId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setTemp(loanQueryDOMapper.selectUniversalLoanFinancialPlanTemp(orderId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }


    @Override
    public RecombinationVO modifyDetail(Long orderId) {
        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setTemp(loanQueryDOMapper.selectUniversalLoanFinancialPlanTemp(orderId));
        return recombinationVO;
    }

    @Override
    public void modifyUpdate(FinancialSchemeModifyUpdateParam param) {

        LoanFinancialPlanTempHisDO his = BeanPlasticityUtills.copy(LoanFinancialPlanTempHisDO.class,param);
        loanFinancialPlanTempHisDOMapper.insertSelective(his);

        LoanFinancialPlanTempDO DO = loanFinancialPlanTempDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));
        if(DO==null){
            LoanFinancialPlanTempDO V = BeanPlasticityUtills.copy(LoanFinancialPlanTempDO.class,param);
            loanFinancialPlanTempDOMapper.insertSelective(V);
        }else {
            LoanFinancialPlanTempDO V = BeanPlasticityUtills.copy(LoanFinancialPlanTempDO.class,param);
            loanFinancialPlanTempDOMapper.updateByPrimaryKeySelective(V);
        }

        Long orderId = Long.valueOf(param.getOrder_id());
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));
        if(loanOrderDO!=null){
            Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();

            if(loanFinancialPlanId!=null){
                LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
                loanFinancialPlanDO.setId(loanFinancialPlanId);
                loanFinancialPlanDO.setFinancialProductId(Long.valueOf(param.getFinancial_product_id()));
                loanFinancialPlanDO.setAppraisal(new BigDecimal(param.getFinancial_appraisal()));
                loanFinancialPlanDO.setBank(param.getFinancial_bank());
                loanFinancialPlanDO.setLoanTime(Integer.valueOf(param.getFinancial_loan_time()));
                loanFinancialPlanDO.setDownPaymentRatio(new BigDecimal(param.getFinancial_down_payment_ratio()));
                loanFinancialPlanDO.setFinancialProductName(param.getFinancial_product_name());
                loanFinancialPlanDO.setSignRate(new BigDecimal(param.getFinancial_sign_rate()));
                loanFinancialPlanDO.setLoanAmount(new BigDecimal(param.getFinancial_loan_amount()));
                loanFinancialPlanDO.setFirstMonthRepay(new BigDecimal(param.getFinancial_first_month_repay()));
                loanFinancialPlanDO.setCarPrice(new BigDecimal(param.getFinancial_car_price()));
                loanFinancialPlanDO.setDownPaymentMoney(new BigDecimal(param.getFinancial_down_payment_money()));
                loanFinancialPlanDO.setBankPeriodPrincipal(new BigDecimal(param.getFinancial_bank_period_principal()));
                loanFinancialPlanDO.setEachMonthRepay(new BigDecimal(param.getFinancial_each_month_repay()));
                loanFinancialPlanDO.setPrincipalInterestSum(new BigDecimal(param.getFinancial_total_repayment_amount()));
                loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
            }
        }
    }

    @Override
    public List<UniversalCustomerOrderVO> queryCustomerOrder(String name) {
        return loanQueryDOMapper.selectUniversalCustomerOrder(name);
    }


}
