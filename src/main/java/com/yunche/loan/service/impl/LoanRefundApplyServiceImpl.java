package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
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

import static com.yunche.loan.config.constant.ApplyOrderStatusConst.APPLY_ORDER_PASS;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_DONE;

@Service
public class LoanRefundApplyServiceImpl implements LoanRefundApplyService {

    @Resource
    private LoanRefundApplyDOMapper loanRefundApplyDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanProcessServiceImpl loanProcessServiceImpl;


    @Override
    public RecombinationVO detail(Long orderId, Long refundId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);

        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO<UniversalInfoVO> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setRefund(loanQueryDOMapper.selectUniversalLoanRefundApply(orderId, refundId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    @Transactional
    public void update(LoanRefundApplyParam param) {

        checkPreCondition(Long.valueOf(param.getOrder_id()));

        if (StringUtils.isBlank(param.getRefund_id())) {
            LoanRefundApplyDO DO = BeanPlasticityUtills.copy(LoanRefundApplyDO.class, param);
            EmployeeDO employeeDO = SessionUtils.getLoginUser();
            DO.setInitiator_id(employeeDO.getId());
            DO.setInitiator_name(employeeDO.getName());
            loanRefundApplyDOMapper.insertSelective(DO);
        } else {
            LoanRefundApplyDO DO = BeanPlasticityUtills.copy(LoanRefundApplyDO.class, param);
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

    /**
     * 发起【申请单】前置校验
     *
     * @param orderId
     */
    private void checkPreCondition(Long orderId) {

        LoanProcessDO loanProcessDO = loanProcessServiceImpl.getLoanProcess(orderId);

        // 【退款申请】
        // 1
        Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanProcessDO.getRemitReview()), "[打款确认]未通过，无法发起[退款申请]");

        // 历史进行中的申请单
        LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(orderId);
        if (null != loanRefundApplyDO) {
            Preconditions.checkArgument(APPLY_ORDER_PASS.equals(loanRefundApplyDO.getStatus()), "当前已存在审核中的[退款申请]");
        }
    }
}
