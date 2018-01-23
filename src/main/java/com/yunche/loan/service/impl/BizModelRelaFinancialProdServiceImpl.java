package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BizModelRelaFinancialProdDOMapper;
import com.yunche.loan.domain.dataObj.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.dataObj.BizModelRelaFinancialProdDO;
import com.yunche.loan.service.BizModelRelaFinancialProdService;
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
public class BizModelRelaFinancialProdServiceImpl implements BizModelRelaFinancialProdService {
    @Autowired
    private BizModelRelaFinancialProdDOMapper bizModelRelaFinancialProdDOMapper;

    @Override
    public ResultBean<Void> insert(BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO) {
        Preconditions.checkArgument(bizModelRelaFinancialProdDO != null, "bizModelRelaFinancialProdDO不能为空");
        int count = bizModelRelaFinancialProdDOMapper.insert(bizModelRelaFinancialProdDO);
//        Preconditions.checkArgument(count > 1, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> batchInsert(List<BizModelRelaFinancialProdDO> bizModelRelaAreaDOList) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(bizModelRelaAreaDOList), "bizModelRelaAreaDOList不能为空");
        for (BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO : bizModelRelaAreaDOList) {
            int count = bizModelRelaFinancialProdDOMapper.insert(bizModelRelaFinancialProdDO);
//            Preconditions.checkArgument(count > 1, "创建失败");
        }
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO) {
        return null;
    }

    @Override
    public ResultBean<Void> delete(Long bizId) {
        return null;
    }

    @Override
    public ResultBean<List<BizModelRelaFinancialProdDO>> getById(Long bizId) {
        return null;
    }
}
