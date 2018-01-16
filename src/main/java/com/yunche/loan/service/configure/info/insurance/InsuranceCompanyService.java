package com.yunche.loan.service.configure.info.insurance;

import com.yunche.loan.dto.configure.info.insurance.InsuranceCompanyDTO;
import com.yunche.loan.dto.configure.info.padding.PaddingCompanyDTO;
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

    ResultBean<InsuranceCompanyDTO> getById(Integer id);

    ResultBean<List<InsuranceCompanyDTO>> query(InsuranceCompanyQuery query);
}
