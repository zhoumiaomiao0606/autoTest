package com.yunche.loan.service.configure.info.padding;

import com.yunche.loan.vo.configure.info.padding.PaddingCompanyVO;
import com.yunche.loan.obj.configure.info.padding.PaddingCompanyDO;
import com.yunche.loan.query.configure.info.address.BaseAreaQuery;
import com.yunche.loan.result.ResultBean;

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
