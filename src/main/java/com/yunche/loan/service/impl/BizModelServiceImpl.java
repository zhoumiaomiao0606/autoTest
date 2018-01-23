package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BizModelDOMapper;
import com.yunche.loan.domain.QueryObj.BizModelQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.viewObj.BizModelRegionVO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.domain.viewObj.BizRelaFinancialProductVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
import com.yunche.loan.service.*;
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
//@Transactional
public class BizModelServiceImpl implements BizModelService {
    @Autowired
    private BizModelDOMapper bizModelDOMapper;

    @Autowired
    private BizModelRelaAreaPartnersService bizModelRelaAreaPartnersService;

    @Autowired
    private BizModelRelaFinancialProdService bizModelRelaFinancialProdService;

    @Override
    public ResultBean<Void> insert(BizModelVO bizModelVO) {
        Preconditions.checkArgument(bizModelVO != null, "bizModelVO");
        BizModelDO bizModelDO = new BizModelDO();
        BeanUtils.copyProperties(bizModelVO, bizModelDO);

        long count = bizModelDOMapper.insert(bizModelDO);
//        Preconditions.checkArgument(bizId < 1, "创建失败");

        List<BizModelRegionVO> bizModelRegionVOList = bizModelVO.getBizModelRegionVOList();
        List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList = Lists.newArrayList();
        for (BizModelRegionVO bizModelRegionVO : bizModelRegionVOList) {
            List<UserGroupVO> userGroupVOList = bizModelRegionVO.getUserGroupVOList();
            if (CollectionUtils.isNotEmpty(userGroupVOList)) {
                for (UserGroupVO userGroupVO : userGroupVOList) {
                    BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                    bizModelRelaAreaPartnersDO.setAreaId(bizModelRegionVO.getAreaId());
                    bizModelRelaAreaPartnersDO.setBizId(bizModelDO.getBizId());
                    bizModelRelaAreaPartnersDO.setGroupId(userGroupVO.getId());
                    bizModelRelaAreaPartnersDOList.add(bizModelRelaAreaPartnersDO);
                }
            } else {
                BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                bizModelRelaAreaPartnersDO.setAreaId(bizModelRegionVO.getAreaId());
                bizModelRelaAreaPartnersDO.setBizId(bizModelDO.getBizId());
                bizModelRelaAreaPartnersDO.setGroupId(0L);
                bizModelRelaAreaPartnersDOList.add(bizModelRelaAreaPartnersDO);
            }
        }
        bizModelRelaAreaPartnersService.batchInsert(bizModelRelaAreaPartnersDOList);

        List<BizRelaFinancialProductVO> financialProductDOList = bizModelVO.getFinancialProductDOList();
        List<BizModelRelaFinancialProdDO> bizModelRelaFinancialProdDOList = Lists.newArrayList();
        for (BizRelaFinancialProductVO bizRelaFinancialProductVO : financialProductDOList) {
            BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO = new BizModelRelaFinancialProdDO();
            bizModelRelaFinancialProdDO.setBizId(bizModelDO.getBizId());
            bizModelRelaFinancialProdDO.setProdId(bizRelaFinancialProductVO.getProdId());
            bizModelRelaFinancialProdDOList.add(bizModelRelaFinancialProdDO);
        }
        bizModelRelaFinancialProdService.batchInsert(bizModelRelaFinancialProdDOList);

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
