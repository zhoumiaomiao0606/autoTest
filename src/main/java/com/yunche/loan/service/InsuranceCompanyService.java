package com.yunche.loan.service;

import com.yunche.loan.domain.vo.InsuranceCompanyVO;
import com.yunche.loan.domain.entity.InsuranceCompanyDO;
import com.yunche.loan.domain.query.InsuranceCompanyQuery;
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
