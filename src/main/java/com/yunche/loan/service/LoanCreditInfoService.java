package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.CreditRecordVO;
import com.yunche.loan.domain.vo.LoanCreditInfoVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
public interface LoanCreditInfoService {

    void save(LoanCreditInfoDO loanCreditInfoDO);

    Long create(LoanCreditInfoDO loanCreditInfoDO);

    Long update(LoanCreditInfoDO loanCreditInfoDO);

    LoanCreditInfoVO getByCustomerId(Long id, Byte type);

    CreditRecordVO detailAll(Long loanCustomerId, Byte creditType);

    void saveCreditInfoHis(Long principalCustId, ApprovalParam approval);
}
