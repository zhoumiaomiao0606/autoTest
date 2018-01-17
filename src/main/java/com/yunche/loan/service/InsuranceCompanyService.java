package com.yunche.loan.service;

import com.yunche.loan.domain.valueObj.InsuranceCompanyVO;
import com.yunche.loan.domain.dataObj.InsuranceCompanyDO;
import com.yunche.loan.domain.QueryObj.InsuranceCompanyQuery;
import com.yunche.loan.config.result.ResultBean;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
public interface InsuranceCompanyService {

    ResultBean<Void> create(InsuranceCompanyDO paddingCompanyDO);

    ResultBean<Void> update(InsuranceCompanyDO paddingCompanyDO);

    ResultBean<Void> delete(Integer id);

    ResultBean<InsuranceCompanyVO> getById(Integer id);

    ResultBean<List<InsuranceCompanyVO>> query(InsuranceCompanyQuery query);
}
