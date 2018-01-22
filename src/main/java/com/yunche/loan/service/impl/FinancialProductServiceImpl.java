package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.FinancialProductDOMapper;
import com.yunche.loan.domain.QueryObj.FinancialQuery;
import com.yunche.loan.domain.dataObj.AreaVO;
import com.yunche.loan.domain.dataObj.BaseAreaDO;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.valueObj.BaseAreaVO;
import com.yunche.loan.service.BaseAreaService;
import com.yunche.loan.service.FinancialProductService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/18.
 */
@Service
@Transactional
public class FinancialProductServiceImpl implements FinancialProductService {

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private BaseAreaService baseAreaService;

    @Override
    public ResultBean<Void> batchInsert(List<FinancialProductDO> financialProductDOs) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(financialProductDOs), "financialProductDOs不能为空");
        for (FinancialProductDO financialProductDO : financialProductDOs) {
            int count = financialProductDOMapper.insert(financialProductDO);
//            Preconditions.checkArgument(count > 1, "创建失败");
        }
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> insert(FinancialProductDO financialProductDO) {
        Preconditions.checkArgument(financialProductDO != null, "financialProductDOs不能为空");
        int count = financialProductDOMapper.insert(financialProductDO);
//        Preconditions.checkArgument(count > 1, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(FinancialProductDO financialProductDO) {
        Preconditions.checkArgument(financialProductDO != null && financialProductDO.getProdId() != null, "financialProductDOs不能为空");
        int count = financialProductDOMapper.updateByPrimaryKeySelective(financialProductDO);
//        Preconditions.checkArgument(count > 1, "更新失败");
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    public ResultBean<Void> delete(Long prodId) {
        Preconditions.checkArgument(prodId != null, "prodId");
        int count = financialProductDOMapper.deleteByPrimaryKey(prodId);
//        Preconditions.checkArgument(count > 1, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<FinancialProductDO> getById(Long prodId) {
        Preconditions.checkNotNull(prodId, "prodId");

        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(prodId);
        Preconditions.checkNotNull(financialProductDO, "prodId，数据不存在.");

        return ResultBean.ofSuccess(financialProductDO);
    }

    @Override
    public ResultBean<List<FinancialProductDO>> getByCondition(FinancialQuery financialQuery) {
//        Preconditions.checkNotNull(financialQuery, financialQuery);
        List<Long> list = Lists.newArrayList();
        if (financialQuery.getAreaId() != null && financialQuery.getProv() != null && financialQuery.getCity() == null) {   // 省级区域
            ResultBean<List<AreaVO>> resultBean = baseAreaService.list();
            List<AreaVO> areaVOList = resultBean.getData();
            for (AreaVO areaVO : areaVOList) {
                if(areaVO.getId().longValue() == financialQuery.getAreaId().longValue()) {
                    List<AreaVO.City> cityList = areaVO.getCityList();
                    if (CollectionUtils.isNotEmpty(cityList)) {
                        for (AreaVO.City city : cityList) {
                            list.add(city.getId());
                        }
                    }
                }
            }
            list.add(financialQuery.getAreaId());
            financialQuery.setCascadeAreaIdList(list);
        }
        if (financialQuery.getAreaId() != null && financialQuery.getProv() != null && financialQuery.getCity() != null) {   // 市级区域
            list.add(financialQuery.getAreaId());
            financialQuery.setCascadeAreaIdList(list);
        }
        if (financialQuery.getAreaId() == 100000000000L){   // 全国区域
            financialQuery.setCascadeAreaIdList(null);
        }
        financialQuery.setAreaId(null);
        List<FinancialProductDO> financialProductDOList = financialProductDOMapper.selectByCondition(financialQuery);
//        Preconditions.checkNotNull(financialProductDOList, "，数据不存在.");

        return ResultBean.ofSuccess(financialProductDOList);
    }
}
