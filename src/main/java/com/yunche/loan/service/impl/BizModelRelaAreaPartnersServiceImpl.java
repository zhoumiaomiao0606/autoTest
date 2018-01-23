package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BizAreaRelaAreaDOMapper;
import com.yunche.loan.dao.mapper.BizModelRelaAreaPartnersDOMapper;
import com.yunche.loan.domain.dataObj.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.service.BizModelRelaAreaPartnersService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/22.
 */
@Service
@Transactional
public class BizModelRelaAreaPartnersServiceImpl implements BizModelRelaAreaPartnersService {
    @Autowired
    private BizModelRelaAreaPartnersDOMapper bizModelRelaAreaPartnersDOMapper;

    @Override
    public ResultBean<Void> insert(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO) {
        Preconditions.checkArgument(bizModelRelaAreaPartnersDO != null, "bizModelRelaAreaPartnersDO不能为空");
        int count = bizModelRelaAreaPartnersDOMapper.insert(bizModelRelaAreaPartnersDO);
//        Preconditions.checkArgument(count > 1, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> batchInsert(List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(bizModelRelaAreaPartnersDOList), "bizModelRelaAreaPartnersDOList不能为空");
        for (BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO : bizModelRelaAreaPartnersDOList) {
            int count = bizModelRelaAreaPartnersDOMapper.insert(bizModelRelaAreaPartnersDO);
//            Preconditions.checkArgument(count > 1, "创建失败");
        }
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO) {
        return null;
    }

    @Override
    public ResultBean<Void> delete(Long bizId) {
        return null;
    }

    @Override
    public ResultBean<List<BizModelRelaAreaPartnersDO>> getById(Long bizId) {
        return null;
    }
}
