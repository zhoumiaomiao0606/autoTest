package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.UniversalCompensationParam;
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
     * @param param
     * @return
     */
    void manualInsert(UniversalCompensationParam param);


    ResultBean detail(UniversalCompensationQuery applicationCompensationQuery);
}
