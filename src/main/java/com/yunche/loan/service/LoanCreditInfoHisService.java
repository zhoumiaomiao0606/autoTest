package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LoanCreditInfoHisDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/30
 */
public interface LoanCreditInfoHisService {

    void save(LoanCreditInfoHisDO loanCreditInfoHisDO);

    void create(LoanCreditInfoHisDO loanCreditInfoHisDO);

    void update(LoanCreditInfoHisDO loanCreditInfoHisDO);

    void updateByCustomerId(LoanCreditInfoHisDO loanCreditInfoHisDO);

    /**
     * 征信申请
     *
     * @param principalCustId
     */
    void saveCreditInfoHis_CreditApply(Long principalCustId);

    /**
     * 银行征信查询
     *
     * @param customers
     */
    void saveCreditInfoHis_BankCreditRecord(List<LoanCustomerDO> customers);

    /**
     * 社会征信查询
     *
     * @param principalCustId
     */
    void saveCreditInfoHis_SocialCreditRecord(Long principalCustId);

    /**
     * 银行征信打回
     *
     * @param principalCustId
     * @param info
     * @param isAutoTask
     */
    void saveCreditInfoHis_BankCreditReject(Long principalCustId, String info, boolean isAutoTask);

    /**
     * 银行征信结果
     *
     * @param customerId
     * @param creditResult
     */
    void saveCreditInfoHis_BankCreditResult(Long customerId, Byte creditResult);

    /**
     * 社会征信结果
     *
     * @param customerId
     * @param creditResult
     */
    void saveCreditInfoHis_SocialCreditResult(Long customerId, Byte creditResult);
}
