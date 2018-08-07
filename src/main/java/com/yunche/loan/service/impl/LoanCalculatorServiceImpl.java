package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.FinancialProductDO;
import com.yunche.loan.domain.entity.ProductRateDO;
import com.yunche.loan.domain.entity.ProductRateDOKey;
import com.yunche.loan.domain.vo.CalcParamVO;
import com.yunche.loan.domain.vo.FinancialProductAndRateVO;
import com.yunche.loan.domain.vo.FinancialProductVO;
import com.yunche.loan.mapper.FinancialProductDOMapper;
import com.yunche.loan.mapper.ProductRateDOMapper;
import com.yunche.loan.service.ComputeModeService;
import com.yunche.loan.service.LoanCalculatorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LoanCalculatorServiceImpl implements LoanCalculatorService{
    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private ComputeModeService computeModeService;

    @Autowired
    private ProductRateDOMapper productRateDOMapper;


    @Override
    public ResultBean getAllProduct(String bankName) {
        Preconditions.checkNotNull(bankName,"请先选择贷款银行");
        List<FinancialProductAndRateVO> product = Lists.newArrayList();

        List<FinancialProductDO> financialProductDOS = financialProductDOMapper.listAll();
        financialProductDOS.stream().filter(Objects::nonNull).filter(e ->e.getStatus().equals(BaseConst.VALID_STATUS) && e.getBankName().equals(bankName)).forEach(e->{
            FinancialProductAndRateVO financialProductAndRateVO = new FinancialProductAndRateVO();
            FinancialProductVO financialProductVO = new FinancialProductVO();
            BeanUtils.copyProperties(e,financialProductVO);
            financialProductAndRateVO.setFinancialProductVO(financialProductVO);
            List<ProductRateDO> productRateDOS = productRateDOMapper.selectByProdId(e.getProdId());
            financialProductAndRateVO.setProductRateDOs(productRateDOS);
            product.add(financialProductAndRateVO);
        });
        return ResultBean.ofSuccess(product);
    }

    @Override
    public ResultBean cal(Long prodId,BigDecimal loanAmt,BigDecimal exeRate,int loanTime,BigDecimal carPrice) {

        Preconditions.checkNotNull(prodId,"请选择具体产品");
        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(prodId);
        Preconditions.checkNotNull(financialProductDO,"产品信息不存在");
        Preconditions.checkNotNull(financialProductDO.getFormulaId(),"产品信息异常");

        int formulaId = financialProductDO.getFormulaId();
        ProductRateDOKey productRateDOKey = new ProductRateDOKey();
        productRateDOKey.setProdId(prodId);
        productRateDOKey.setLoanTime(loanTime);
        ProductRateDO productRateDO = productRateDOMapper.selectByPrimaryKey(productRateDOKey);
        Preconditions.checkNotNull(productRateDO,"产品利率信息不存在,请配置后操作");
        BigDecimal bankRate = productRateDO.getBankRate();
        ResultBean<CalcParamVO> calc = computeModeService.calc(formulaId, loanAmt, exeRate, bankRate, loanTime, carPrice);
        return calc;
    }
}
