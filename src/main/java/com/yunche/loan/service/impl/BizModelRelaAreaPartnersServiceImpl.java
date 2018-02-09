package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BizAreaRelaAreaDOMapper;
import com.yunche.loan.dao.mapper.BizModelRelaAreaPartnersDOMapper;
import com.yunche.loan.domain.dataObj.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.dataObj.FinancialProductDO;
import com.yunche.loan.domain.viewObj.BizModelRegionVO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.domain.viewObj.FinancialProductVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
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
            BizModelRelaAreaPartnersDO instance = getByAllId(bizModelRelaAreaPartnersDO).getData();
            if (instance != null) {
                return ResultBean.ofError("该区域的合伙人已存在, 请勿重复添加");
            }
            int count = bizModelRelaAreaPartnersDOMapper.insert(bizModelRelaAreaPartnersDO);
//            Preconditions.checkArgument(count > 1, "创建失败");
        }
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO) {
        Preconditions.checkArgument(bizModelRelaAreaPartnersDO != null, "bizModelRelaAreaPartnersDO不能为空");
        int count = bizModelRelaAreaPartnersDOMapper.update(bizModelRelaAreaPartnersDO);
//        Preconditions.checkArgument(count > 1, "创建失败");
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    public ResultBean<Void> batchUpdate(List<BizModelRelaAreaPartnersDO>  bizModelRelaAreaPartnersDOList) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(bizModelRelaAreaPartnersDOList), "bizModelRelaAreaPartnersDOList不能为空");
        for (BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO : bizModelRelaAreaPartnersDOList) {
            int count = bizModelRelaAreaPartnersDOMapper.update(bizModelRelaAreaPartnersDO);
//            Preconditions.checkArgument(count > 1, "创建失败");
        }
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    public ResultBean<Void> delete(Long bizId) {
        return null;
    }

    @Override
    public ResultBean<Void> deleteRelaPartner(Long bizId, Long areaId, Long groupId) {
        Preconditions.checkArgument(bizId != null && areaId != null && groupId != null, "prodId");
        int count = bizModelRelaAreaPartnersDOMapper.deleteByPrimaryKey(bizId, areaId, groupId);
//        Preconditions.checkArgument(count > 1, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> addRelaPartner(BizModelRegionVO bizModelRegionVO) {
        if (CollectionUtils.isNotEmpty(bizModelRegionVO.getUserGroupVOList())) {
            List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList = Lists.newArrayList();
            for (UserGroupVO userGroupVO : bizModelRegionVO.getUserGroupVOList()) {
                BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                bizModelRelaAreaPartnersDO.setBizId(bizModelRegionVO.getBizId());
                bizModelRelaAreaPartnersDO.setAreaId(bizModelRegionVO.getAreaId());
                bizModelRelaAreaPartnersDO.setGroupId(userGroupVO.getId());
                bizModelRelaAreaPartnersDOList.add(bizModelRelaAreaPartnersDO);
            }
            batchInsert(bizModelRelaAreaPartnersDOList);
        } else {
            BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
            bizModelRelaAreaPartnersDO.setBizId(bizModelRegionVO.getBizId());
            bizModelRelaAreaPartnersDO.setAreaId(bizModelRegionVO.getAreaId());
            bizModelRelaAreaPartnersDO.setGroupId(0L);
            BizModelRelaAreaPartnersDO instance = getByAllId(bizModelRelaAreaPartnersDO).getData();
            if (instance != null) {
                return ResultBean.ofError("该区域的合伙人已存在, 请勿重复添加");
            }
            insert(bizModelRelaAreaPartnersDO);
        }
        return ResultBean.ofSuccess(null, "添加成功");
    }

    @Override
    public ResultBean<List<BizModelRelaAreaPartnersDO>> getById(Long bizId) {
        Preconditions.checkNotNull(bizId, "bizId");

        List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList = bizModelRelaAreaPartnersDOMapper.queryById(bizId);
        Preconditions.checkNotNull(bizModelRelaAreaPartnersDOList, "bizModelRelaAreaPartnersDOList，数据不存在.");

        return ResultBean.ofSuccess(bizModelRelaAreaPartnersDOList);
    }

    @Override
    public ResultBean<BizModelRelaAreaPartnersDO> getByAllId(BizModelRelaAreaPartnersDO paramDO) {
        Preconditions.checkNotNull(paramDO, "bizModelRelaAreaPartnersDO");

        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = bizModelRelaAreaPartnersDOMapper.query(paramDO);
//        Preconditions.checkNotNull(bizModelRelaAreaPartnersDO, "bizModelRelaAreaPartnersDO，数据不存在.");

        return ResultBean.ofSuccess(bizModelRelaAreaPartnersDO);
    }
}
