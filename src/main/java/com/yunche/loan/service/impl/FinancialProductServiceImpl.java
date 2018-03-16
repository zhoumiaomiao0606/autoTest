package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ProductRateDO;
import com.yunche.loan.domain.param.FinancialProductParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.FinancialProductDOMapper;
import com.yunche.loan.domain.query.FinancialQuery;
import com.yunche.loan.domain.entity.FinancialProductDO;
import com.yunche.loan.mapper.ProductRateDOMapper;
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
public class FinancialProductServiceImpl implements FinancialProductService {

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private BaseAreaService baseAreaService;

    @Autowired
    private ProductRateDOMapper productRateDOMapper;

    @Override
    @Transactional
    public ResultBean<Void> batchInsert(List<FinancialProductDO> financialProductDOs) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(financialProductDOs), "financialProductDOs不能为空");
        for (FinancialProductDO financialProductDO : financialProductDOs) {
            int count = financialProductDOMapper.insert(financialProductDO);
            Preconditions.checkArgument(count > 0, "创建失败");
        }
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> insert(FinancialProductParam financialProductParam) {

        Preconditions.checkArgument(financialProductParam != null, "financialProductDOs不能为空");
        int count = financialProductDOMapper.insert(financialProductParam);
        Preconditions.checkArgument(count > 0, "创建金融产品失败");

        List<FinancialProductParam.ProductRate> productRateList = financialProductParam.getProductRateList();
        Preconditions.checkNotNull(productRateList, "费率未设置");

        for (FinancialProductParam.ProductRate tmpProductRate : productRateList) {
            ProductRateDO productRateDO = new ProductRateDO();
            productRateDO.setProdId(financialProductParam.getProdId());
            productRateDO.setLoanTime(tmpProductRate.getLoanTime());
            productRateDO.setBankRate(tmpProductRate.getBankRate());
            int insertCount = productRateDOMapper.insert(productRateDO);
            Preconditions.checkArgument(insertCount > 0, "创建银行费率失败");
        }

        return ResultBean.ofSuccess(financialProductParam.getProdId(), "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> update(FinancialProductParam financialProductParam) {
        Preconditions.checkArgument(financialProductParam != null && financialProductParam.getProdId() != null, "financialProductDOs不能为空");
        int count = financialProductDOMapper.updateByPrimaryKeySelective(financialProductParam);
        List<FinancialProductParam.ProductRate> productRates = financialProductParam.getProductRateList();
        Preconditions.checkNotNull(productRates, "费率未设置");
        //更新前先按照产品ID删除之前的记录
        productRateDOMapper.deleteByProdId(financialProductParam.getProdId());
        //插入本次产品费率信息
        for (FinancialProductParam.ProductRate tmpProductRate : productRates) {
            ProductRateDO productRateDO = new ProductRateDO();
            productRateDO.setProdId(financialProductParam.getProdId());
            productRateDO.setLoanTime(tmpProductRate.getLoanTime());
            productRateDO.setBankRate(tmpProductRate.getBankRate());
            productRateDOMapper.insert(productRateDO);
        }
        Preconditions.checkArgument(count > 0, "更新失败");
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> delete(Long prodId) {
        Preconditions.checkArgument(prodId != null, "prodId");

        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(prodId);
        Preconditions.checkNotNull(financialProductDO, "prodId，数据不存在.");
        productRateDOMapper.deleteByProdId(prodId);
        financialProductDO.setStatus((byte) 2);
        long count = financialProductDOMapper.updateByPrimaryKeySelective(financialProductDO);
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> disable(Long prodId) {
        Preconditions.checkArgument(prodId != null, "prodId");

        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(prodId);
        Preconditions.checkNotNull(financialProductDO, "prodId，数据不存在.");

        financialProductDO.setStatus((byte) 1);
        long count = financialProductDOMapper.updateByPrimaryKeySelective(financialProductDO);
        return ResultBean.ofSuccess(null, "停用成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> enable(Long prodId) {
        Preconditions.checkArgument(prodId != null, "prodId");

        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(prodId);
        Preconditions.checkNotNull(financialProductDO, "prodId，数据不存在.");

        financialProductDO.setStatus((byte) 0);
        long count = financialProductDOMapper.updateByPrimaryKeySelective(financialProductDO);
        return ResultBean.ofSuccess(null, "启用成功");
    }

    @Override
    public ResultBean<FinancialProductVO> getById(Long prodId) {
        Preconditions.checkNotNull(prodId, "prodId");

        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(prodId);
        Preconditions.checkNotNull(financialProductDO, "prodId，数据不存在.");

        FinancialProductVO financialProductVO = getFinancialProductVO(financialProductDO);

        return ResultBean.ofSuccess(financialProductVO);
    }

    private FinancialProductVO getFinancialProductVO(FinancialProductDO financialProductDO) {
        FinancialProductVO financialProductVO = new FinancialProductVO();
        BeanUtils.copyProperties(financialProductDO, financialProductVO);

        ResultBean<BaseAreaVO> resultBean = baseAreaService.getById(financialProductVO.getAreaId());
        BaseAreaVO baseAreaVO = resultBean.getData();
        if (baseAreaVO.getLevel() == 2) {
            Long provId = baseAreaVO.getParentAreaId();
            Long cityId = baseAreaVO.getAreaId();
            financialProductVO.setProvId(provId);
            financialProductVO.setCityId(cityId);
        }
        if (baseAreaVO.getLevel() == 1 || baseAreaVO.getLevel() == 0) {
            Long provId = baseAreaVO.getAreaId();
            financialProductVO.setProvId(provId);
        }
        return financialProductVO;
    }

    @Override
    public ResultBean<List<FinancialProductVO>> getByCondition(FinancialQuery financialQuery) {
//        Preconditions.checkNotNull(financialQuery, financialQuery);
        List<Long> list = Lists.newArrayList();
        if (financialQuery.getAreaId() != null && financialQuery.getProv() != null && financialQuery.getCity() == null) {   // 省级区域
            ResultBean<List<CascadeAreaVO>> resultBean = baseAreaService.list();
            List<CascadeAreaVO> cascadeAreaVOList = resultBean.getData();
            for (CascadeAreaVO cascadeAreaVO : cascadeAreaVOList) {
                if (cascadeAreaVO.getId().longValue() == financialQuery.getAreaId().longValue()) {
                    List<CascadeAreaVO.City> cityList = cascadeAreaVO.getCityList();
                    if (CollectionUtils.isNotEmpty(cityList)) {
                        for (CascadeAreaVO.City city : cityList) {
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
        if (financialQuery.getAreaId() != null && financialQuery.getAreaId() == 100000000000L) {   // 全国区域
            financialQuery.setCascadeAreaIdList(null);
        }
        financialQuery.setAreaId(null);
        List<FinancialProductDO> financialProductDOList = financialProductDOMapper.selectByCondition(financialQuery);
        Preconditions.checkNotNull(financialProductDOList, "，数据不存在.");

        List<FinancialProductVO> financialProductVOList = Lists.newArrayList();
        for (FinancialProductDO financialProductDO : financialProductDOList) {
            financialProductVOList.add(getFinancialProductVO(financialProductDO));
        }
        return ResultBean.ofSuccess(financialProductVOList);
    }

    @Override
    public ResultBean<List<CascadeFinancialProductVO>> listByPartnerId(Long partnerId) {
        Preconditions.checkNotNull(partnerId, "合伙人ID不能为空");


        // 根据合伙人获取授权银行列表

        // 根据银行获取金融产品列表

        // 根据金融产品获取对应银行利率


        return ResultBean.ofSuccess(null);
    }
}
