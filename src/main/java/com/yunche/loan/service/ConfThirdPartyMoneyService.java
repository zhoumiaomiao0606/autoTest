package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfThirdPartyMoneyDO;
import com.yunche.loan.domain.query.ConfThirdPartyMoneyQuery;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/5
 */
public interface ConfThirdPartyMoneyService {

    Long create(ConfThirdPartyMoneyDO confThirdPartyMoneyDO);

    Void update(ConfThirdPartyMoneyDO confThirdPartyMoneyDO);

    Void delete(Long id);

    ConfThirdPartyMoneyDO detail(Long id);

    ResultBean<List<ConfThirdPartyMoneyDO>> query(ConfThirdPartyMoneyQuery query);
}
