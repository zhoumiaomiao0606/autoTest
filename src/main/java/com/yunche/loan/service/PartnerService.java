package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.PartnerQuery;
import com.yunche.loan.domain.dataObj.PartnerDO;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.PartnerVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
public interface PartnerService {
    ResultBean<Long> create(PartnerParam partnerParam);

    ResultBean<Void> update(PartnerDO partnerDO);

    ResultBean<Void> delete(Long id);

    ResultBean<PartnerVO> getById(Long id);

    ResultBean<List<UserGroupVO>> query(PartnerQuery query);

    ResultBean<List<AuthVO>> listBizModel(BaseQuery query);

    ResultBean<Void> deleteRelaBizModels(Long id, String bizModelIds);
}
