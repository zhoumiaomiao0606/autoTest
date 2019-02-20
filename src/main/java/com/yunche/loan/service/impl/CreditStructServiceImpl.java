package com.yunche.loan.service.impl;

import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.CreditStructParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.CreditStructService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/19
 */
@Service
public class CreditStructServiceImpl implements CreditStructService {

    @Autowired
    private CreditStructBlackAshSignDOMapper creditStructBlackAshSignDOMapper;

    @Autowired
    private CreditStructGuaranteeCreditCardDetailDOMapper creditStructGuaranteeCreditCardDetailDOMapper;

    @Autowired
    private CreditStructGuaranteeLoanDetailDOMapper creditStructGuaranteeLoanDetailDOMapper;

    @Autowired
    private CreditStructQueryCountDOMapper creditStructQueryCountDOMapper;

    @Autowired
    private CreditStructSumDOMapper creditStructSumDOMapper;

    @Autowired
    private CreditStructTradeDetailDOMapper creditStructTradeDetailDOMapper;

    @Autowired
    private CreditStructTradeDetailLoanDOMapper creditStructTradeDetailLoanDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;


    @Override
    public RecombinationVO detail(Long orderId) {
        Assert.notNull(orderId, "订单号不能为空");

        RecombinationVO recombinationVO = new RecombinationVO();

        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setCreditStruct(loanQueryService.selectUniversalCreditStruct(orderId));

        return recombinationVO;
    }

    @Override
    @Transactional
    public void save(CreditStructParam param) {
        Assert.notNull(param, "customerId不能为空");
        Assert.notNull(param.getCustomerId(), "customerId不能为空");

        Long customerId = param.getCustomerId();

        CreditStructBlackAshSignDO exist_1 = creditStructBlackAshSignDOMapper.selectByPrimaryKey(customerId);
        CreditStructBlackAshSignDO creditStructBlackAshSign = param.getCreditStructBlackAshSign();
        if (null == exist_1) {
            if (null != creditStructBlackAshSign) {
                int count = creditStructBlackAshSignDOMapper.insertSelective(creditStructBlackAshSign);
                Assert.isTrue(count > 0, "保存失败");
            }
        } else {
            int count = creditStructBlackAshSignDOMapper.updateByPrimaryKeySelective(creditStructBlackAshSign);
            Assert.isTrue(count > 0, "保存失败");
        }

        CreditStructQueryCountDO exist_2 = creditStructQueryCountDOMapper.selectByPrimaryKey(customerId);
        CreditStructQueryCountDO creditStructQueryCount = param.getCreditStructQueryCount();
        if (null == exist_2) {
            if (null != creditStructQueryCount) {
                int count = creditStructQueryCountDOMapper.insertSelective(creditStructQueryCount);
                Assert.isTrue(count > 0, "保存失败");
            }
        } else {
            int count = creditStructQueryCountDOMapper.updateByPrimaryKeySelective(creditStructQueryCount);
            Assert.isTrue(count > 0, "保存失败");
        }

        CreditStructSumDO exist_3 = creditStructSumDOMapper.selectByPrimaryKey(customerId);
        CreditStructSumDO creditStructSum = param.getCreditStructSum();
        if (null == exist_3) {
            if (null != creditStructSum) {
                int count = creditStructSumDOMapper.insertSelective(creditStructSum);
                Assert.isTrue(count > 0, "保存失败");
            }
        } else {
            int count = creditStructSumDOMapper.updateByPrimaryKeySelective(creditStructSum);
            Assert.isTrue(count > 0, "保存失败");
        }

        // del old
        creditStructTradeDetailDOMapper.deleteByCustomerId(customerId);
        // insert now
        List<CreditStructTradeDetailDO> creditStructTradeDetailDOList = param.getCreditStructTradeDetail();
        for (int i = 0; i < creditStructTradeDetailDOList.size(); i++) {
            CreditStructTradeDetailDO creditStructTradeDetailDO = creditStructTradeDetailDOList.get(i);
            int count = creditStructTradeDetailDOMapper.insertSelective(creditStructTradeDetailDO);
            Assert.isTrue(count > 0, "保存失败");
        }

        // del old
        creditStructTradeDetailLoanDOMapper.deleteByCustomerId(customerId);
        // insert now
        List<CreditStructTradeDetailLoanDO> creditStructTradeDetailLoanDOListList = param.getCreditStructTradeDetailLoan();
        for (int i = 0; i < creditStructTradeDetailLoanDOListList.size(); i++) {
            CreditStructTradeDetailLoanDO creditStructTradeDetailLoanDO = creditStructTradeDetailLoanDOListList.get(i);
            int count = creditStructTradeDetailLoanDOMapper.insertSelective(creditStructTradeDetailLoanDO);
            Assert.isTrue(count > 0, "保存失败");
        }

        // del old
        creditStructGuaranteeCreditCardDetailDOMapper.deleteByCustomerId(customerId);
        // insert now
        List<CreditStructGuaranteeCreditCardDetailDO> creditStructGuaranteeCreditCardDetailDOList = param.getCreditStructGuaranteeCreditCardDetail();
        for (int i = 0; i < creditStructGuaranteeCreditCardDetailDOList.size(); i++) {
            CreditStructGuaranteeCreditCardDetailDO creditStructGuaranteeCreditCardDetailDO = creditStructGuaranteeCreditCardDetailDOList.get(i);
            int count = creditStructGuaranteeCreditCardDetailDOMapper.insertSelective(creditStructGuaranteeCreditCardDetailDO);
            Assert.isTrue(count > 0, "保存失败");
        }

        // del old
        creditStructGuaranteeLoanDetailDOMapper.deleteByCustomerId(customerId);
        // insert now
        List<CreditStructGuaranteeLoanDetailDO> creditStructGuaranteeLoanDetailDOList = param.getCreditStructGuaranteeLoanDetail();
        for (int i = 0; i < creditStructGuaranteeLoanDetailDOList.size(); i++) {
            CreditStructGuaranteeLoanDetailDO creditStructGuaranteeLoanDetailDO = creditStructGuaranteeLoanDetailDOList.get(i);
            int count = creditStructGuaranteeLoanDetailDOMapper.insertSelective(creditStructGuaranteeLoanDetailDO);
            Assert.isTrue(count > 0, "保存失败");
        }
    }
}
