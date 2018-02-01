package com.yunche.loan.service;

import com.yunche.loan.domain.viewObj.InsuranceCompanyVO;
import com.yunche.loan.domain.dataObj.InsuranceCompanyDO;
import com.yunche.loan.domain.queryObj.InsuranceCompanyQuery;
import com.yunche.loan.config.result.ResultBean;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
public interface InsuranceCompanyService {

    ResultBean<Long> create(InsuranceCompanyDO paddingCompanyDO);

    ResultBean<Void> update(InsuranceCompanyDO paddingCompanyDO);

    ResultBean<Void> delete(Long id);

    ResultBean<InsuranceCompanyVO> getById(Long id);

    ResultBean<List<InsuranceCompanyVO>> query(InsuranceCompanyQuery query);
}
