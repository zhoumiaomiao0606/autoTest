package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BizModelDOMapper;
import com.yunche.loan.domain.queryObj.BizModelQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.viewObj.*;
import com.yunche.loan.domain.viewObj.CascadeAreaVO;
import com.yunche.loan.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

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

    @Autowired
    private BaseAreaService baseAreaService;

    @Autowired
    private FinancialProductService financialProductService;

    @Autowired
    private UserGroupService userGroupService;

    @Override
    public ResultBean<Void> insert(BizModelVO bizModelVO) {
        Preconditions.checkArgument(bizModelVO != null, "bizModelVO");
        Preconditions.checkNotNull(bizModelVO.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(bizModelVO.getStatus().byteValue()) || INVALID_STATUS.equals(bizModelVO.getStatus().byteValue()),
                "状态非法");

        BizModelDO bizModelDO = new BizModelDO();
        BeanUtils.copyProperties(bizModelVO, bizModelDO);

        long count = bizModelDOMapper.insert(bizModelDO);

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
                    bizModelRelaAreaPartnersDO.setProv(bizModelRegionVO.getProv());
                    bizModelRelaAreaPartnersDO.setCity(bizModelRegionVO.getCity());
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
        if (CollectionUtils.isNotEmpty(bizModelRelaAreaPartnersDOList)) {
            bizModelRelaAreaPartnersService.batchInsert(bizModelRelaAreaPartnersDOList);
        }

        List<BizRelaFinancialProductVO> financialProductDOList = bizModelVO.getFinancialProductDOList();
        List<BizModelRelaFinancialProdDO> bizModelRelaFinancialProdDOList = Lists.newArrayList();
        for (BizRelaFinancialProductVO bizRelaFinancialProductVO : financialProductDOList) {
            BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO = new BizModelRelaFinancialProdDO();
            bizModelRelaFinancialProdDO.setBizId(bizModelDO.getBizId());
            bizModelRelaFinancialProdDO.setProdId(bizRelaFinancialProductVO.getProdId());
            bizModelRelaFinancialProdDOList.add(bizModelRelaFinancialProdDO);
        }
        if (CollectionUtils.isNotEmpty(bizModelRelaFinancialProdDOList)) {
            bizModelRelaFinancialProdService.batchInsert(bizModelRelaFinancialProdDOList);
        }

        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(BizModelVO bizModelVO) {
        Preconditions.checkArgument(bizModelVO != null, "bizModelVO");
        BizModelDO bizModelDO = new BizModelDO();
        BeanUtils.copyProperties(bizModelVO, bizModelDO);

        long count = bizModelDOMapper.updateByPrimaryKeySelective(bizModelDO);

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
                    bizModelRelaAreaPartnersDO.setProv(bizModelRegionVO.getProv());
                    bizModelRelaAreaPartnersDO.setCity(bizModelRegionVO.getCity());
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
        bizModelRelaAreaPartnersService.batchUpdate(bizModelRelaAreaPartnersDOList);

        List<BizRelaFinancialProductVO> financialProductDOList = bizModelVO.getFinancialProductDOList();
        List<BizModelRelaFinancialProdDO> bizModelRelaFinancialProdDOList = Lists.newArrayList();
        for (BizRelaFinancialProductVO bizRelaFinancialProductVO : financialProductDOList) {
            BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO = new BizModelRelaFinancialProdDO();
            bizModelRelaFinancialProdDO.setBizId(bizModelDO.getBizId());
            bizModelRelaFinancialProdDO.setProdId(bizRelaFinancialProductVO.getProdId());
            bizModelRelaFinancialProdDOList.add(bizModelRelaFinancialProdDO);
        }
        bizModelRelaFinancialProdService.batchUpdate(bizModelRelaFinancialProdDOList);

        return ResultBean.ofSuccess(null, "修改成功");
    }

    @Override
    public ResultBean<Void> delete(Long bizId) {
        Preconditions.checkNotNull(bizId, "bizId");

        BizModelDO bizModelDO = bizModelDOMapper.selectByPrimaryKey(bizId);
        Preconditions.checkNotNull(bizModelDO, "bizId，数据不存在.");

        bizModelDO.setStatus(2);
        long count = bizModelDOMapper.updateByPrimaryKeySelective(bizModelDO);
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> disable(Long bizId) {
        Preconditions.checkNotNull(bizId, "bizId");

        BizModelDO bizModelDO = bizModelDOMapper.selectByPrimaryKey(bizId);
        Preconditions.checkNotNull(bizModelDO, "bizId，数据不存在.");

        bizModelDO.setStatus(1);
        long count = bizModelDOMapper.updateByPrimaryKeySelective(bizModelDO);
        return ResultBean.ofSuccess(null, "停用成功");
    }

    @Override
    public ResultBean<Void> enable(Long bizId) {
        Preconditions.checkNotNull(bizId, "bizId");

        BizModelDO bizModelDO = bizModelDOMapper.selectByPrimaryKey(bizId);
        Preconditions.checkNotNull(bizModelDO, "bizId，数据不存在.");

        bizModelDO.setStatus(0);
        long count = bizModelDOMapper.updateByPrimaryKeySelective(bizModelDO);
        return ResultBean.ofSuccess(null, "启用成功");
    }

    @Override
    public ResultBean<BizModelVO> getById(Long bizId) {
        Preconditions.checkNotNull(bizId, "bizId");

        BizModelDO bizModelDO = bizModelDOMapper.selectByPrimaryKey(bizId);
        Preconditions.checkNotNull(bizModelDO, "bizId，数据不存在.");

        BizModelVO bizModelVO = new BizModelVO();
        BeanUtils.copyProperties(bizModelDO, bizModelVO);
        List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList = bizModelRelaAreaPartnersService.getById(bizId).getData();
        if (CollectionUtils.isNotEmpty(bizModelRelaAreaPartnersDOList)) {
            Map<Long, List<Long>> areaWithPartnerMap = Maps.newHashMap();
            for (BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO : bizModelRelaAreaPartnersDOList) {
                if (areaWithPartnerMap.containsKey(bizModelRelaAreaPartnersDO.getAreaId())) {
                    List<Long> partnerList = areaWithPartnerMap.get(bizModelRelaAreaPartnersDO.getAreaId());
                    partnerList.add(bizModelRelaAreaPartnersDO.getGroupId());
                } else {
                    areaWithPartnerMap.put(bizModelRelaAreaPartnersDO.getAreaId(), Lists.newArrayList(bizModelRelaAreaPartnersDO.getGroupId()));
                }
            }
            List<BizModelRegionVO> bizModelRegionVOList = Lists.newArrayList();
            bizModelVO.setBizModelRegionVOList(bizModelRegionVOList);
            Set<Long> areaIdSet = areaWithPartnerMap.keySet();
            List<BaseAreaVO> baseAreaVOList = baseAreaService.getByIdList(Lists.newArrayList(areaIdSet)).getData();
            for (BaseAreaVO baseAreaVO : baseAreaVOList) {
                BizModelRegionVO bizModelRegionVO = new BizModelRegionVO();
                bizModelRegionVO.setAreaId(baseAreaVO.getAreaId());
                if (baseAreaVO.getLevel() == 2) {
                    Long provId = baseAreaVO.getParentAreaId();
                    Long cityId = baseAreaVO.getAreaId();
                    bizModelRegionVO.setProvId(provId);
                    bizModelRegionVO.setProv(baseAreaVO.getParentAreaName());
                    bizModelRegionVO.setCityId(cityId);
                    bizModelRegionVO.setCity(baseAreaVO.getAreaName());
                }
                if (baseAreaVO.getLevel() == 1 || baseAreaVO.getLevel() == 0) {
                    Long provId = baseAreaVO.getAreaId();
                    bizModelRegionVO.setProvId(provId);
                    bizModelRegionVO.setProv(baseAreaVO.getAreaName());
                }

                List<Long> parterGroupIdList = areaWithPartnerMap.get(baseAreaVO.getAreaId());
//                List<UserGroupVO> userGroupVOList = Lists.newArrayList();
//                for (Long groupId : parterGroupIdList) {
//                    UserGroupVO userGroupVO = new UserGroupVO();
//                    userGroupVO.setId(groupId);
////                    userGroupService.
//                    userGroupVOList.add(userGroupVO);
//                }
                List<UserGroupVO> userGroupVOList = userGroupService.batchGetById(parterGroupIdList).getData();
//                if (CollectionUtils.isEmpty(userGroupVOList)) continue;
                bizModelRegionVO.setUserGroupVOList(userGroupVOList);
                bizModelRegionVOList.add(bizModelRegionVO);
            }
        }

        List<BizModelRelaFinancialProdDO> bizModelRelaFinancialProdDOList = bizModelRelaFinancialProdService.getById(bizId).getData();
        List<BizRelaFinancialProductVO> financialProductDOList = Lists.newArrayList();
        for (BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO : bizModelRelaFinancialProdDOList) {
            FinancialProductVO financialProductVO = financialProductService.getById(bizModelRelaFinancialProdDO.getProdId()).getData();
            BizRelaFinancialProductVO bizRelaFinancialProductVO = new BizRelaFinancialProductVO();
            BeanUtils.copyProperties(financialProductVO, bizRelaFinancialProductVO);
            financialProductDOList.add(bizRelaFinancialProductVO);
        }
        bizModelVO.setFinancialProductDOList(financialProductDOList);

        return ResultBean.ofSuccess(bizModelVO);
    }

    @Override
    public ResultBean<List<BizModelVO>> getByCondition(BizModelQuery bizModelQuery) {
        List<Long> list = Lists.newArrayList();
        if (bizModelQuery.getAreaId() != null && bizModelQuery.getProv() != null && bizModelQuery.getCity() == null) {   // 省级区域
            ResultBean<List<CascadeAreaVO>> resultBean = baseAreaService.list();
            List<CascadeAreaVO> cascadeAreaVOList = resultBean.getData();
            for (CascadeAreaVO cascadeAreaVO : cascadeAreaVOList) {
                if (cascadeAreaVO.getId().longValue() == bizModelQuery.getAreaId().longValue()) {
                    List<CascadeAreaVO.City> cityList = cascadeAreaVO.getCityList();
                    if (CollectionUtils.isNotEmpty(cityList)) {
                        for (CascadeAreaVO.City city : cityList) {
                            list.add(city.getId());
                        }
                    }
                }
            }
            list.add(bizModelQuery.getAreaId());
            list.add(100000000000L);
            bizModelQuery.setCascadeAreaIdList(list);
        }
        if (bizModelQuery.getAreaId() != null && bizModelQuery.getProv() != null && bizModelQuery.getCity() != null) {   // 市级区域
            list.add(bizModelQuery.getAreaId());
            list.add(100000000000L);
            bizModelQuery.setCascadeAreaIdList(list);
        }
        if (bizModelQuery.getAreaId() != null && bizModelQuery.getAreaId() == 100000000000L) {   // 全国区域
            bizModelQuery.setCascadeAreaIdList(null);
        }
        bizModelQuery.setAreaId(null);

        List<BizModelDO> bizModelDOList = bizModelDOMapper.selectByCondition(bizModelQuery);
        Preconditions.checkNotNull(bizModelDOList, "，数据不存在.");

        List<BizModelVO> bizModelVOList = Lists.newArrayList();
        for (BizModelDO bizModelDO : bizModelDOList) {
            BizModelVO bizModelVO = new BizModelVO();
            BeanUtils.copyProperties(bizModelDO, bizModelVO);
            if (!bizModelVOList.contains(bizModelVO)) {
                bizModelVOList.add(bizModelVO);
            }
        }
        return ResultBean.ofSuccess(bizModelVOList);
    }
}
