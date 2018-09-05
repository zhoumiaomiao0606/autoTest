package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ThirdPartyFundDO;

import java.util.List;

public interface ThirdPartyFundService {
    ThirdPartyFundDO detail(Long aLong);

    ResultBean<Void> update(ThirdPartyFundDO param);

    List<ThirdPartyFundDO> list();
}
