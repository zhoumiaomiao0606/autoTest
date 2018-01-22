package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BizModelDOMapper;
import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.dataObj.BizModelDO;
import com.yunche.loan.domain.dataObj.BizModelRelaAreaDO;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.BizModelRegionVO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.service.BizModelRelaAreaService;
import com.yunche.loan.service.BizModelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/22.
 */
@Service
@Transactional
public class BizModelRelaAreaServiceImpl implements BizModelRelaAreaService {
    @Autowired
    private BizModelDOMapper bizModelDOMapper;


    @Override
    public ResultBean<Void> insert(BizModelRelaAreaDO bizModelRelaAreaDO) {
        return null;
    }

    @Override
    public ResultBean<Void> BatchInsert(List<BizModelRelaAreaDO> bizModelRelaAreaDOList) {
        return null;
    }

    @Override
    public ResultBean<Void> update(BizModelRelaAreaDO bizModelRelaAreaDO) {
        return null;
    }

    @Override
    public ResultBean<Void> delete(Long bizId) {
        return null;
    }

    @Override
    public ResultBean<List<BizModelRelaAreaDO>> getById(Long bizId) {
        return null;
    }
}
