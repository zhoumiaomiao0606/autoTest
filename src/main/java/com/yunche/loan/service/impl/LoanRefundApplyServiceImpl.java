package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanRefundApplyDO;
import com.yunche.loan.domain.param.LoanRefundApplyParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.LoanRefundApplyDOMapper;
import com.yunche.loan.service.LoanRefundApplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class LoanRefundApplyServiceImpl implements LoanRefundApplyService {


    @Resource
    private LoanRefundApplyDOMapper loanRefundApplyDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public RecombinationVO detail(Long orderId, Long refundId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);

        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO<UniversalInfoVO> recombinationVO = new RecombinationVO<UniversalInfoVO>();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setRefund(loanQueryDOMapper.selectUniversalLoanRefundApply(orderId,refundId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    public void update(LoanRefundApplyParam param) {
        if(StringUtils.isBlank(param.getRefund_id())){
            LoanRefundApplyDO DO = BeanPlasticityUtills.copy(LoanRefundApplyDO.class,param);
            EmployeeDO employeeDO = SessionUtils.getLoginUser();
            DO.setInitiator_id(employeeDO.getId());
            DO.setInitiator_name(employeeDO.getName());
            loanRefundApplyDOMapper.insertSelective(DO);
        }else {
            LoanRefundApplyDO DO = BeanPlasticityUtills.copy(LoanRefundApplyDO.class,param);
            EmployeeDO employeeDO = SessionUtils.getLoginUser();
            DO.setId(Long.valueOf(param.getRefund_id()));
            DO.setInitiator_id(employeeDO.getId());
            DO.setInitiator_name(employeeDO.getName());
            loanRefundApplyDOMapper.updateByPrimaryKeySelective(DO);
        }

    }


    @Override
    public List<UniversalCustomerOrderVO> queryRefundCustomerOrder(String name) {
        return loanQueryDOMapper.selectUniversalRefundCustomerOrder(SessionUtils.getLoginUser().getId(), name);
    }
}
