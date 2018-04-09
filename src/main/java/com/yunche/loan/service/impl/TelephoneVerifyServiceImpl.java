package com.yunche.loan.service.impl;

import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanCarInfoDOMapper;
import com.yunche.loan.mapper.LoanFinancialPlanDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.TelephoneVerifyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class TelephoneVerifyServiceImpl implements TelephoneVerifyService {
    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {

        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setCredits(loanQueryDOMapper.selectUniversalCreditInfo(orderId));
        recombinationVO.setHome(loanQueryDOMapper.selectUniversalHomeVisitInfo(orderId));
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_telephone_verify",orderId));
        recombinationVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderId(orderId));
        recombinationVO.setSupplement(loanQueryDOMapper.selectUniversalSupplementInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalMaterialRecord(orderId));
        recombinationVO.setCustomers(customers);

        return recombinationVO;
    }

    @Override
    public void update(TelephoneVerifyParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));

        Long loanCarInfoId = loanOrderDO.getLoanCarInfoId();
        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();

        if(loanOrderDO!=null){
            if(loanCarInfoId!=null){
                LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
                loanCarInfoDO.setId(loanCarInfoId);
                loanCarInfoDO.setGpsNum(StringUtils.isBlank(param.getCar_gps_num())?null:Integer.valueOf(param.getCar_gps_num()));
                loanCarInfoDO.setCarKey(StringUtils.isBlank(param.getCar_key())?null:new Byte(param.getCar_key()));
                loanCarInfoDOMapper.updateByPrimaryKeySelective(loanCarInfoDO);
            }
            if(loanFinancialPlanId!=null){
                LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
                loanFinancialPlanDO.setId(loanFinancialPlanId);
                loanFinancialPlanDO.setCashDeposit(StringUtils.isBlank(param.getFinancial_cash_deposit())?null:new BigDecimal(param.getFinancial_cash_deposit()));
                loanFinancialPlanDO.setExtraFee(StringUtils.isBlank(param.getFinancial_extra_fee())?null:new BigDecimal(param.getFinancial_extra_fee()));
                loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
            }
        }






    }
}
