package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BizModelDOMapper;
import com.yunche.loan.dao.mapper.FinancialProductDOMapper;
import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.QueryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.BizModelDO;
import com.yunche.loan.domain.dataObj.BizModelRelaAreaDO;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.AreaVO;
import com.yunche.loan.domain.viewObj.BizModelRegionVO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.service.BaseAreaService;
import com.yunche.loan.service.BizModelRelaAreaService;
import com.yunche.loan.service.BizModelService;
import com.yunche.loan.service.FinancialProductService;
import org.apache.commons.collections.CollectionUtils;
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
public class BizModelServiceImpl implements BizModelService {
    @Autowired
    private BizModelDOMapper bizModelDOMapper;

    @Autowired
    private BizModelRelaAreaService bizModelRelaAreaService;

    @Override
    public ResultBean<Void> insert(BizModelVO bizModelVO) {
        Preconditions.checkArgument(bizModelVO != null, "bizModelVO");
        BizModelDO bizModelDO = new BizModelDO();
        BeanUtils.copyProperties(bizModelVO, bizModelDO);

        int count = bizModelDOMapper.insert(bizModelDO);
        Preconditions.checkArgument(count > 1, "创建失败");

        List<BizModelRegionVO> bizModelRegionVOList = bizModelVO.getBizModelRegionVOList();
        BizModelRelaAreaDO bizModelRelaAreaDO = new BizModelRelaAreaDO();

        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(BizModelVO bizModelVO) {
        return null;
    }

    @Override
    public ResultBean<Void> delete(Long bizId) {
        return null;
    }

    @Override
    public ResultBean<FinancialProductDO> getById(Long bizId) {
        return null;
    }

    @Override
    public ResultBean<List<FinancialProductDO>> getByCondition(BizModelQuery bizModelQuery) {
        return null;
    }
}
