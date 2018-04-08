package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.BizModelRelaAreaPartnersDOMapper;
import com.yunche.loan.domain.entity.BizModelRelaAreaPartnersDO;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.BizModelRelaAreaPartnersService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/22.
 */
@Service
public class BizModelRelaAreaPartnersServiceImpl implements BizModelRelaAreaPartnersService {

    @Autowired
    private BizModelRelaAreaPartnersDOMapper bizModelRelaAreaPartnersDOMapper;

    @Override
    @Transactional
    public ResultBean<Void> insert(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO) {
        Preconditions.checkArgument(bizModelRelaAreaPartnersDO != null, "bizModelRelaAreaPartnersDO不能为空");

        bizModelRelaAreaPartnersDO.setGmtCreate(new Date());
        bizModelRelaAreaPartnersDO.setGmtModify(new Date());
        int count = bizModelRelaAreaPartnersDOMapper.insert(bizModelRelaAreaPartnersDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> batchInsert(List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(bizModelRelaAreaPartnersDOList), "bizModelRelaAreaPartnersDOList不能为空");

        for (BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO : bizModelRelaAreaPartnersDOList) {
            BizModelRelaAreaPartnersDO instance = getByAllId(bizModelRelaAreaPartnersDO).getData();
            if (instance != null) {
                return ResultBean.ofError("该区域的合伙人已存在, 请勿重复添加");
            }
            ResultBean<Void> resultBean = insert(bizModelRelaAreaPartnersDO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        }
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> update(BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO) {
        Preconditions.checkArgument(bizModelRelaAreaPartnersDO != null, "bizModelRelaAreaPartnersDO不能为空");
        int count = bizModelRelaAreaPartnersDOMapper.update(bizModelRelaAreaPartnersDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> batchUpdate(List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList) {
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(bizModelRelaAreaPartnersDOList), "bizModelRelaAreaPartnersDOList不能为空");
        for (BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO : bizModelRelaAreaPartnersDOList) {
            int count = bizModelRelaAreaPartnersDOMapper.update(bizModelRelaAreaPartnersDO);
            Preconditions.checkArgument(count > 0, "创建失败");
        }
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> delete(Long bizId) {
        return null;
    }

    @Override
    @Transactional
    public ResultBean<Void> deleteRelaPartner(Long bizId, Long areaId, Long groupId) {
        Preconditions.checkArgument(bizId != null && areaId != null && groupId != null, "参数有误");

        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDOKey = new BizModelRelaAreaPartnersDO();
        bizModelRelaAreaPartnersDOKey.setBizId(bizId);
        bizModelRelaAreaPartnersDOKey.setAreaId(areaId);
        bizModelRelaAreaPartnersDOKey.setGroupId(groupId);
        int count = bizModelRelaAreaPartnersDOMapper.deleteByPrimaryKey(bizModelRelaAreaPartnersDOKey);
        Preconditions.checkArgument(count > 0, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> addRelaPartner(BizModelRegionVO bizModelRegionVO) {
        if (CollectionUtils.isNotEmpty(bizModelRegionVO.getPartnerVOList())) {
            List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList = Lists.newArrayList();
            for (PartnerVO partnerVO : bizModelRegionVO.getPartnerVOList()) {
                BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                bizModelRelaAreaPartnersDO.setBizId(bizModelRegionVO.getBizId());
                bizModelRelaAreaPartnersDO.setAreaId(bizModelRegionVO.getAreaId());
                bizModelRelaAreaPartnersDO.setGroupId(partnerVO.getId());
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
