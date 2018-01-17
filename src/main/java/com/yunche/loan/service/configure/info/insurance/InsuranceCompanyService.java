package com.yunche.loan.service.configure.info.insurance;

import com.yunche.loan.vo.configure.info.insurance.InsuranceCompanyVO;
import com.yunche.loan.obj.configure.info.insurance.InsuranceCompanyDO;
import com.yunche.loan.query.configure.info.insurance.InsuranceCompanyQuery;
import com.yunche.loan.result.ResultBean;

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
