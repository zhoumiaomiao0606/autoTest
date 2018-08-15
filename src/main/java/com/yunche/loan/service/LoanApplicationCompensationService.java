package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.query.UniversalCompensationQuery;

public interface LoanApplicationCompensationService {
    /**
     * 导入文件
     * @param key
     * @return
     */
    void batchInsert(String key);

    /**
     * 手工导入
     * @param loanApplyCompensationDO
     * @return
     */
    void manualInsert(LoanApplyCompensationDO loanApplyCompensationDO);


    ResultBean detail(UniversalCompensationQuery applicationCompensationQuery);
}
