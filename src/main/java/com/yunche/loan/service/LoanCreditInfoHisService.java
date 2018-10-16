package com.yunche.loan.service;

/**
 * @author liuzhe
 * @date 2018/9/30
 */
public interface LoanCreditInfoHisService {


    /**
     * 创建征信查询历史记录  --> 银行/社会
     *
     * @param principalCustId
     * @param loanAmount
     */
    void saveCreditInfoHis_CreditApply(Long principalCustId, Byte loanAmount);

    /**
     * 银行征信     提交时间/查询人
     * <p>
     * [银行征信]提交时间  --> 作为 银行征信查询时间
     *
     * @param principalCustId
     */
    void saveCreditInfoHis_BankCreditRecord(Long principalCustId);

    /**
     * 社会征信     提交时间/查询人
     * <p>
     * [社会征信]提交时间  --> 作为 社会征信查询时间
     *
     * @param principalCustId
     */
    void saveCreditInfoHis_SocialCreditRecord(Long principalCustId);

    /**
     * 银行征信     打回时间/人/备注
     *
     * @param principalCustId
     * @param info
     * @param isAutoTask
     */
    void saveCreditInfoHis_BankCreditReject(Long principalCustId, String info, boolean isAutoTask);

    /**
     * 银行征信查询历史    -- 打回时间/人/备注     --- 单个客户
     *
     * @param customerId
     * @param info
     * @param isAutoTask
     */
    void saveCreditInfoHis_BankCreditReject_SingleCustomer(Long customerId, String info, boolean isAutoTask);

    /**
     * 社会征信     打回时间/人/备注
     *
     * @param principalCustId
     * @param info
     * @param isAutoTask
     */
    void saveCreditInfoHis_SocialCreditReject(Long principalCustId, String info, boolean isAutoTask);

    /**
     * 银行征信 结果      --- 单个客户
     *
     * @param principalCustId
     * @param creditResult
     */
    void saveCreditInfoHis_BankCreditResult(Long principalCustId, Byte creditResult);

    /**
     * 社会征信 结果      --- 单个客户
     *
     * @param customerId
     * @param creditResult
     */
    void saveCreditInfoHis_SocialCreditResult(Long customerId, Byte creditResult);
}
