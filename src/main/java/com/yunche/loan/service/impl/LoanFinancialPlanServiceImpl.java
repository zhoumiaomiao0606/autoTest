package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.FinancialProductDO;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.LoanFinancialPlanParam;
import com.yunche.loan.domain.vo.CalcParamVO;
import com.yunche.loan.domain.vo.LoanFinancialPlanVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.ComputeModeService;
import com.yunche.loan.service.LoanBaseInfoService;
import com.yunche.loan.service.LoanFinancialPlanService;
import com.yunche.loan.service.LoanProcessOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanAmountConst.*;

/**
 * @author liuzhe
 * @date 2018/3/9
 */
@Service
@Transactional
public class LoanFinancialPlanServiceImpl implements LoanFinancialPlanService {

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    ComputeModeService computeModeService;

    @Autowired
    private LoanProcessOrderService loanProcessOrderService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;
    @Autowired
    private LoanBaseInfoService loanBaseInfoService;

    @Override
    public ResultBean<Long> create(LoanFinancialPlanDO loanFinancialPlanDO) {
        Preconditions.checkNotNull(loanFinancialPlanDO, "金融方案不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getCarPrice(), "车辆价格不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getFinancialProductId(), "金融产品不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getBank(), "贷款银行不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getSignRate(), "签约利率不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getLoanAmount(), "贷款金额不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getLoanTime(), "贷款期数不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getDownPaymentRatio(), "首付比例不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getDownPaymentMoney(), "首付额不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getBankPeriodPrincipal(), "银行分期本金不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getBankFee(), "银行手续费不能为空");
//        Preconditions.checkNotNull(loanFinancialPlanDO.getPrincipalInterestSum(), "本息合计不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getFirstMonthRepay(), "首月还款额不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getEachMonthRepay(), "每月还款额不能为空");

        loanFinancialPlanDO.setGmtCreate(new Date());
        loanFinancialPlanDO.setGmtModify(new Date());
        loanFinancialPlanDO.setStatus(VALID_STATUS);
        int count = loanFinancialPlanDOMapper.insertSelective(loanFinancialPlanDO);
        Preconditions.checkArgument(count > 0, "创建贷款金融方案失败");

        return ResultBean.ofSuccess(loanFinancialPlanDO.getId(), "创建贷款金融方案成功");
    }

    @Override
    public ResultBean<Void> update(LoanFinancialPlanDO loanFinancialPlanDO) {
        Preconditions.checkArgument(null != loanFinancialPlanDO && null != loanFinancialPlanDO.getId(), "金融方案ID不能为空");

        loanFinancialPlanDO.setGmtModify(new Date());
        int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
        Preconditions.checkArgument(count > 0, "编辑贷款金融方案失败");

        return ResultBean.ofSuccess(null, "编辑贷款金融方案成功");
    }

    @Override
    public ResultBean<LoanFinancialPlanVO> calc(LoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam, "金融产品不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getCarPrice(), "车辆价格不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getFinancialProductId(), "金融产品ID不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getSignRate(), "签约利率不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getLoanAmount(), "贷款金额不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getLoanTime(), "贷款期数不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getBankRate(), "银行利率不能为空");

        //
        loanFinancialPlanParam.setDownPaymentMoney(loanFinancialPlanParam.getCarPrice().subtract(loanFinancialPlanParam.getLoanAmount()));
        // 获取公式ID
        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(loanFinancialPlanParam.getFinancialProductId());
        Preconditions.checkNotNull(financialProductDO, "金融产品不存在");
        // 公式ID
        Integer formulaId = financialProductDO.getFormulaId();

        // 根据贷款期数,获取对应银行基准利率
        ResultBean<CalcParamVO> resultBean = computeModeService.calc(formulaId, loanFinancialPlanParam.getLoanAmount(), loanFinancialPlanParam.getSignRate(),
                loanFinancialPlanParam.getBankRate(), loanFinancialPlanParam.getLoanTime(), loanFinancialPlanParam.getCarPrice());
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        LoanFinancialPlanVO loanFinancialPlanVO = new LoanFinancialPlanVO();

        CalcParamVO calcParamVO = resultBean.getData();
        //约定出参中首付比率和银行分期比例应答以 N% 的形式返回
        BigDecimal base = new BigDecimal(100);
        if (null != calcParamVO) {
            //首付比例
            loanFinancialPlanVO.setDownPaymentRatio(base.multiply(loanFinancialPlanParam.getDownPaymentMoney().divide(loanFinancialPlanParam.getCarPrice(), 4, BigDecimal.ROUND_HALF_EVEN)));
            // 首付额 =首付比率*车价
            loanFinancialPlanVO.setDownPaymentMoney(loanFinancialPlanParam.getDownPaymentMoney());
            // 本息合计(还款总额)
            loanFinancialPlanVO.setPrincipalInterestSum(calcParamVO.getTotalRepayment());

            // 银行分期本金
            loanFinancialPlanVO.setBankPeriodPrincipal(calcParamVO.getBankPeriodPrincipal());
            // 银行手续费
            loanFinancialPlanVO.setBankFee(calcParamVO.getBankFee());
            // 首月还款
            loanFinancialPlanVO.setFirstMonthRepay(calcParamVO.getFirstRepayment());
            // 每月还款
            loanFinancialPlanVO.setEachMonthRepay(calcParamVO.getEachMonthRepay());
            //银行分期比例
            loanFinancialPlanVO.setStagingRatio(calcParamVO.getStagingRatio());
            //金融服务费 ：贷款金额
            loanFinancialPlanVO.setLoanInterest(calcParamVO.getLoanInterest());

            loanFinancialPlanVO.setLoanRate(String.valueOf(loanFinancialPlanParam.getLoanAmount().multiply(new BigDecimal("100")).divide(loanFinancialPlanParam.getCarPrice(),2,BigDecimal.ROUND_HALF_UP)));
        }

        return ResultBean.ofSuccess(loanFinancialPlanVO, "计算成功");
    }

    @Override
    public ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam) {
        ResultBean<LoanFinancialPlanVO> resultBean = calc(loanFinancialPlanParam);
        return resultBean;
    }

    @Override
    public ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        Long loanFinancialPlanId = loanOrderDOMapper.getLoanFinancialPlanIdById(orderId);
        Map map = financialProductDOMapper.selectProductInfoByOrderId(orderId);

        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);
        LoanFinancialPlanVO loanFinancialPlanVO = new LoanFinancialPlanVO();
        if (null != loanFinancialPlanDO) {
            BeanUtils.copyProperties(loanFinancialPlanDO, loanFinancialPlanVO);
        }
        if (map != null) {
            loanFinancialPlanVO.setCategorySuperior((String) map.get("categorySuperior"));
            loanFinancialPlanVO.setBankRate((BigDecimal) map.get("bankRate"));
            loanFinancialPlanVO.setStagingRatio((BigDecimal) map.get("stagingRatio"));
            if(loanFinancialPlanDO !=null) {
                if (loanFinancialPlanDO.getBankPeriodPrincipal() != null && loanFinancialPlanDO.getLoanAmount() != null) {
                    loanFinancialPlanVO.setFinancialServiceFee(String.valueOf(loanFinancialPlanDO.getBankPeriodPrincipal().subtract(loanFinancialPlanDO.getLoanAmount())));
                }
                if (loanFinancialPlanDO.getLoanAmount() != null && loanFinancialPlanDO.getCarPrice() != null) {
                    loanFinancialPlanVO.setLoanRate(String.valueOf(loanFinancialPlanDO.getLoanAmount().multiply(new BigDecimal("100")).divide(loanFinancialPlanDO.getCarPrice(),2,BigDecimal.ROUND_HALF_UP)));
                }
            }
        }

        formatData(loanFinancialPlanVO);
        return ResultBean.ofSuccess(loanFinancialPlanVO);
    }

    /**
     * 格式化数据
     *
     * @param loanFinancialPlanVO
     */
    private void formatData(LoanFinancialPlanVO loanFinancialPlanVO) {
        if (loanFinancialPlanVO.getFirstMonthRepay() != null) {
            loanFinancialPlanVO.setFirstMonthRepay(loanFinancialPlanVO.getFirstMonthRepay().setScale(2, BigDecimal.ROUND_UP));
        }
        if (loanFinancialPlanVO.getBankPeriodPrincipal() != null) {
            loanFinancialPlanVO.setBankPeriodPrincipal(loanFinancialPlanVO.getBankPeriodPrincipal().setScale(2, BigDecimal.ROUND_UP));
        }
        if (loanFinancialPlanVO.getEachMonthRepay() != null) {
            loanFinancialPlanVO.setEachMonthRepay(loanFinancialPlanVO.getEachMonthRepay().setScale(2, BigDecimal.ROUND_UP));
        }
    }


    @Override
    @Transactional
    public ResultBean<Long> createOrUpdateLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam, "贷款金融方案不能为空");

        loanFinancialPlanParam.setPlayCompany(loanFinancialPlanParam.getPaddingCompany());

        if (null == loanFinancialPlanParam.getId()) {
            // 创建
            return createLoanFinancialPlan(loanFinancialPlanParam);
        } else {
            // 编辑
            return updateLoanFinancialPlan(loanFinancialPlanParam);
        }
    }

    /**
     * insert贷款金融方案
     *
     * @param loanFinancialPlanParam
     */
    private ResultBean<Long> createLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam.getOrderId(), "业务单号不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanFinancialPlanParam, loanFinancialPlanDO);

        // insert
        ResultBean<Long> resultBean = create(loanFinancialPlanDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        // 关联
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(loanFinancialPlanParam.getOrderId());
        loanOrderDO.setLoanFinancialPlanId(loanFinancialPlanDO.getId());
        ResultBean<Void> updateRelaResult = loanProcessOrderService.update(loanOrderDO);
        Preconditions.checkArgument(updateRelaResult.getSuccess(), updateRelaResult.getMsg());

        // 更新预计贷款
        updateExpectLoanAmount(loanFinancialPlanParam.getOrderId(), loanFinancialPlanParam.getLoanAmount());

        return resultBean;
    }

    /**
     * update贷款金融方案
     *
     * @param loanFinancialPlanParam
     */
    private ResultBean<Long> updateLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam.getOrderId(), "业务单号不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanFinancialPlanParam, loanFinancialPlanDO);
        loanFinancialPlanDO.setGmtModify(new Date());

        int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
        Preconditions.checkArgument(count > 0, "编辑贷款金融方案失败");

        // 更新预计贷款
        updateExpectLoanAmount(loanFinancialPlanParam.getOrderId(), loanFinancialPlanParam.getLoanAmount());

        return ResultBean.ofSuccess(null, "保存贷款金融方案成功");
    }

    /**
     * 更新预计贷款
     *
     * @param orderId
     * @param actualLoanAmount
     */
    private void updateExpectLoanAmount(Long orderId, BigDecimal actualLoanAmount) {
        // 实际贷款额
        Preconditions.checkNotNull(actualLoanAmount, "贷款额不能为空");
        double actualLoanAmount_ = actualLoanAmount.doubleValue();

        // 预计贷款额
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");
        Preconditions.checkNotNull(loanOrderDO.getLoanBaseInfoId(), "业务单基本信息丢失");

        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        loanBaseInfoDO.setId(loanOrderDO.getLoanBaseInfoId());
        if (actualLoanAmount_ < ACTUAL_LOAN_AMOUNT_13W) {
            loanBaseInfoDO.setLoanAmount(EXPECT_LOAN_AMOUNT_LT_13W);
        } else if (actualLoanAmount_ >= ACTUAL_LOAN_AMOUNT_13W && actualLoanAmount_ < ACTUAL_LOAN_AMOUNT_20W) {
            loanBaseInfoDO.setLoanAmount(EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W);
        } else if (actualLoanAmount_ >= ACTUAL_LOAN_AMOUNT_20W) {
            loanBaseInfoDO.setLoanAmount(EXPECT_LOAN_AMOUNT_EQT_20W);
        }

        ResultBean<Void> result = loanBaseInfoService.update(loanBaseInfoDO);
        Preconditions.checkArgument(result.getSuccess(), result.getMsg());
    }
}

