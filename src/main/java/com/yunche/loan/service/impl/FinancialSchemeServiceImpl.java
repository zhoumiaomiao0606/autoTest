package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.FinancialSchemeModifyUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.FinancialSchemeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.yunche.loan.config.constant.ApplyOrderStatusConst.APPLY_ORDER_INIT;
import static com.yunche.loan.config.constant.ApplyOrderStatusConst.APPLY_ORDER_PASS;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;

@Service
public class FinancialSchemeServiceImpl implements FinancialSchemeService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanFinancialPlanTempHisDOMapper loanFinancialPlanTempHisDOMapper;

    @Resource
    private LoanProcessDOMapper loanProcessDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        RecombinationVO<FinancialSchemeVO> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(financialSchemeVO);
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    public RecombinationVO verifyDetail(Long orderId, Long hisId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setDiff(loanQueryDOMapper.selectUniversalLoanFinancialPlanTempHis(orderId, hisId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }


    @Override
    public RecombinationVO modifyDetail(Long orderId, Long hisId) {
        RecombinationVO recombinationVO = new RecombinationVO();
        /*recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setDiff(loanQueryDOMapper.selectUniversalLoanFinancialPlanTempHis(hisId));*/
        if (hisId == null) {
            recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        } else {
            recombinationVO.setInfo(loanQueryDOMapper.selectUniversalLoanFinancialPlanTempHis(orderId, hisId));
        }
        return recombinationVO;
    }

    @Override
    @Transactional
    public ResultBean<Long> modifyUpdate(FinancialSchemeModifyUpdateParam param) {

        if (StringUtils.isBlank(param.getHis_id())) {

            checkPreCondition(Long.valueOf(param.getOrder_id()));

            //单号为空,视为新增
            LoanFinancialPlanTempHisDO his = BeanPlasticityUtills.copy(LoanFinancialPlanTempHisDO.class, param);
            EmployeeDO employeeDO = SessionUtils.getLoginUser();
            his.setInitiator_id(employeeDO.getId());
            his.setInitiator_name(employeeDO.getName());
            his.setStatus(APPLY_ORDER_INIT);
            his.setStart_time(new Date());

            int count = loanFinancialPlanTempHisDOMapper.insertSelective(his);
            Preconditions.checkArgument(count > 0, "插入失败");

            return ResultBean.ofSuccess(his.getId());

        } else {
            //单号为空,视为新增
            LoanFinancialPlanTempHisDO his = BeanPlasticityUtills.copy(LoanFinancialPlanTempHisDO.class, param);
            his.setId(Long.valueOf(param.getHis_id()));
            EmployeeDO employeeDO = SessionUtils.getLoginUser();
            his.setInitiator_id(employeeDO.getId());
            his.setInitiator_name(employeeDO.getName());
            loanFinancialPlanTempHisDOMapper.updateByPrimaryKeySelective(his);

            return ResultBean.ofSuccess(his.getId());
        }
    }

    @Override
    @Transactional
    public void migration(Long orderId, Long hisId, String action) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, new Byte("0"));
        if (loanOrderDO == null) {
            throw new BizException("订单不存在");
        }
        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();
        if (loanFinancialPlanId == null) {
            throw new BizException("金融方案单不存在");
        }
        LoanFinancialPlanTempHisDO param = loanFinancialPlanTempHisDOMapper.selectByPrimaryKey(hisId);
        if (param == null) {
            throw new BizException("金融方案修改单不存在");
        }
        //先对金融方案修改单进行更新
        LoanFinancialPlanTempHisDO updateV = new LoanFinancialPlanTempHisDO();
        updateV.setId(hisId);
        if (!StringUtils.isBlank(action)) {
            updateV.setStatus(new Byte(action));
        }
        EmployeeDO employeeDO = SessionUtils.getLoginUser();
        updateV.setAuditor_id(employeeDO.getId());
        updateV.setAuditor_name(employeeDO.getName());
        updateV.setEnd_time(new Timestamp(System.currentTimeMillis()));
        loanFinancialPlanTempHisDOMapper.updateByPrimaryKeySelective(updateV);
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        loanFinancialPlanDO.setId(loanFinancialPlanId);
        loanFinancialPlanDO.setFinancialProductId(Long.valueOf(param.getFinancial_product_id()));
        loanFinancialPlanDO.setAppraisal(param.getFinancial_appraisal());
        loanFinancialPlanDO.setBank(param.getFinancial_bank());
        loanFinancialPlanDO.setLoanTime(Integer.valueOf(param.getFinancial_loan_time()));
        loanFinancialPlanDO.setDownPaymentRatio(param.getFinancial_down_payment_ratio());
        loanFinancialPlanDO.setFinancialProductName(param.getFinancial_product_name());
        loanFinancialPlanDO.setSignRate(param.getFinancial_sign_rate());
        loanFinancialPlanDO.setLoanAmount(param.getFinancial_loan_amount());
        loanFinancialPlanDO.setFirstMonthRepay(param.getFinancial_first_month_repay());
        loanFinancialPlanDO.setCarPrice(param.getFinancial_car_price());
        loanFinancialPlanDO.setDownPaymentMoney(param.getFinancial_down_payment_money());
        loanFinancialPlanDO.setBankPeriodPrincipal(param.getFinancial_bank_period_principal());
        loanFinancialPlanDO.setEachMonthRepay(param.getFinancial_each_month_repay());
        loanFinancialPlanDO.setPrincipalInterestSum(param.getFinancial_total_repayment_amount());
        loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
    }

    @Override
    public List<UniversalCustomerOrderVO> queryModifyCustomerOrder(String name) {
        return loanQueryDOMapper.selectUniversalModifyCustomerOrder(SessionUtils.getLoginUser().getId(), name);
    }

    /**
     * 新建【申请单】前置校验
     *
     * @param orderId
     */
    private void checkPreCondition(Long orderId) {

        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        // 1
        Preconditions.checkArgument(TASK_PROCESS_DONE.equals(loanProcessDO.getTelephoneVerify()), "[电审]未通过，无法发起[金融方案修改申请]");

        // [放款审批]已通过
        if (TASK_PROCESS_DONE.equals(loanProcessDO.getLoanReview())) {
            // 是否已退款
            Preconditions.checkArgument(TASK_PROCESS_REFUND.equals(loanProcessDO.getRemitReview()), "[放款审批]已通过，无法发起[金融方案修改申请]");
        } else {
            // 是否有进行中的[申请单]
            LoanFinancialPlanTempHisDO loanFinancialPlanTempHisDO = loanFinancialPlanTempHisDOMapper.lastByOrderId(orderId);
            if (null != loanFinancialPlanTempHisDO) {
                Preconditions.checkArgument(APPLY_ORDER_PASS.equals(loanFinancialPlanTempHisDO.getStatus()), "当前已存在审核中的[金融方案修改申请]");
            }
        }
    }
}
