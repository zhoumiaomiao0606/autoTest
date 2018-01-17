package com.yunche.loan.service;

import com.yunche.loan.domain.valueObj.PaddingCompanyVO;
import com.yunche.loan.domain.dataObj.PaddingCompanyDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
public interface PaddingCompanyService {

    ResultBean<Void> create(PaddingCompanyDO paddingCompanyDO);

    ResultBean<Void> update(PaddingCompanyDO paddingCompanyDO);

    ResultBean<Void> delete(Integer id);

    ResultBean<PaddingCompanyVO> getById(Integer id);

    ResultBean<List<PaddingCompanyVO>> query(BaseAreaQuery query);
}
