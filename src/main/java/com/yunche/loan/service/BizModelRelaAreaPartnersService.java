package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.viewObj.BizModelRegionVO;
import com.yunche.loan.domain.viewObj.BizModelVO;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
public interface BizModelRelaAreaPartnersService {

    ResultBean<Void> insert(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    ResultBean<Void> batchInsert(List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList);

    ResultBean<Void> update(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

    ResultBean<Void> batchUpdate(List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList);

    ResultBean<Void> delete(Long bizId);

    ResultBean<Void> deleteRelaPartner(Long bizId, Long areaId, Long groupId);

    ResultBean<Void> addRelaPartner(BizModelRegionVO bizModelRegionVO);

    ResultBean<List<BizModelRelaAreaPartnersDO>> getById(Long bizId);

    ResultBean<BizModelRelaAreaPartnersDO> getByAllId(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO);

}
