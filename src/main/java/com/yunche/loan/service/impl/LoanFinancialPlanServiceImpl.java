package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.FinancialProductDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.ProductRateDO;
import com.yunche.loan.domain.entity.ProductRateDOKey;
import com.yunche.loan.domain.param.AppLoanFinancialPlanParam;
import com.yunche.loan.domain.vo.AppLoanFinancialPlanVO;
import com.yunche.loan.domain.vo.CalcParamVO;
import com.yunche.loan.domain.vo.LoanFinancialPlanVO;
import com.yunche.loan.mapper.FinancialProductDOMapper;
import com.yunche.loan.mapper.LoanFinancialPlanDOMapper;
import com.yunche.loan.mapper.ProductRateDOMapper;
import com.yunche.loan.service.ComputeModeService;
import com.yunche.loan.service.LoanFinancialPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/3/9
 */
@Service
@Transactional
public class LoanFinancialPlanServiceImpl implements LoanFinancialPlanService {

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private ProductRateDOMapper productRateDOMapper;

    @Autowired
    ComputeModeService computeModeService;


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
        Preconditions.checkNotNull(loanFinancialPlanDO.getPrincipalInterestSum(), "本息合计不能为空");
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
    public ResultBean<LoanFinancialPlanVO> calc(LoanFinancialPlanDO loanFinancialPlanDO) {
        Preconditions.checkNotNull(loanFinancialPlanDO, "金融产品不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getCarPrice(), "车辆价格不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getFinancialProductId(), "金融产品ID不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getSignRate(), "签约利率不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getLoanAmount(), "贷款金额不能为空");
        Preconditions.checkNotNull(loanFinancialPlanDO.getLoanTime(), "贷款期数不能为空");

        // 获取公式ID
        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(loanFinancialPlanDO.getFinancialProductId());
        Preconditions.checkNotNull(financialProductDO, "金融产品不存在");
        // 公式ID
        Integer formulaId = financialProductDO.getFormulaId();

        // 根据贷款期数,获取对应银行基准利率
        ProductRateDOKey productRateDOKey = new ProductRateDOKey();
        productRateDOKey.setProdId(loanFinancialPlanDO.getFinancialProductId());
        productRateDOKey.setLoanTime(loanFinancialPlanDO.getLoanTime());
        ProductRateDO productRateDO = productRateDOMapper.selectByPrimaryKey(productRateDOKey);
        Preconditions.checkNotNull(productRateDO, "银行费率不存在");
        BigDecimal bankBaseRate = productRateDO.getBankRate();

        ResultBean<CalcParamVO> resultBean = computeModeService.calc(formulaId, loanFinancialPlanDO.getLoanAmount(), loanFinancialPlanDO.getSignRate(),
                bankBaseRate, loanFinancialPlanDO.getLoanTime() / 12, loanFinancialPlanDO.getCarPrice());
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        LoanFinancialPlanVO loanFinancialPlanVO = new LoanFinancialPlanVO();


        CalcParamVO calcParamVO = resultBean.getData();
        if (null != calcParamVO) {
            //首付比例
            loanFinancialPlanVO.setDownPaymentRatio(loanFinancialPlanDO.getDownPaymentMoney().divide(loanFinancialPlanDO.getCarPrice(),4));
            // 首付额 =首付比率*车价
            loanFinancialPlanVO.setDownPaymentMoney(loanFinancialPlanDO.getDownPaymentMoney());
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
        }

        return ResultBean.ofSuccess(loanFinancialPlanVO, "计算成功");
    }
}
