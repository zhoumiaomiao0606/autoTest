package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.BizModelParam;
import com.yunche.loan.mapper.BizModelDOMapper;
import com.yunche.loan.domain.query.BizModelQuery;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.domain.vo.CascadeAreaVO;
import com.yunche.loan.mapper.BizModelRelaAreaPartnersDOMapper;
import com.yunche.loan.mapper.BizModelRelaFinancialProdDOMapper;
import com.yunche.loan.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * Created by zhouguoliang on 2018/1/22.
 */
@Service
public class BizModelServiceImpl implements BizModelService {

    public static final Long NOT_LIMIT_PARTNER = 0L;

    @Autowired
    private BizModelDOMapper bizModelDOMapper;

    @Autowired
    private BizModelRelaAreaPartnersService bizModelRelaAreaPartnersService;

    @Autowired
    private BizModelRelaFinancialProdService bizModelRelaFinancialProdService;

    @Autowired
    private BizModelRelaAreaPartnersDOMapper bizModelRelaAreaPartnersDOMapper;

    @Autowired
    private BizModelRelaFinancialProdDOMapper bizModelRelaFinancialProdDOMapper;

    @Autowired
    private BaseAreaService baseAreaService;

    @Autowired
    private FinancialProductService financialProductService;

    @Autowired
    private PartnerService partnerService;


    @Override
    @Transactional
    public ResultBean<Long> insert(BizModelParam bizModelParam) {
        Preconditions.checkNotNull(bizModelParam, "参数不能为空");
        Preconditions.checkNotNull(bizModelParam.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(bizModelParam.getStatus().byteValue()) || INVALID_STATUS.equals(bizModelParam.getStatus().byteValue()),
                "状态非法");

        BizModelDO bizModelDO = new BizModelDO();
        bizModelDO.setGmtCreate(new Date());
        bizModelDO.setGmtModify(new Date());
        BeanUtils.copyProperties(bizModelParam, bizModelDO);

        long count = bizModelDOMapper.insertSelective(bizModelDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        Long bizId = bizModelDO.getBizId();

        // 绑定
        List<BizModelParam.RelaAreaIdPartnerIdList> relaAreaIdPartnerIdList = bizModelParam.getRelaAreaIdPartnerIdList();
        List<Long> financialProductIdList = bizModelParam.getFinancialProductIdList();

        bindAreaIdPartnerIdList(bizId, relaAreaIdPartnerIdList);
        bindFinancialProductIdList(bizId, financialProductIdList);

        return ResultBean.ofSuccess(bizId, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> update(BizModelParam bizModelParam) {
        Preconditions.checkNotNull(bizModelParam, "参数不能为空");

        BizModelDO bizModelDO = new BizModelDO();
        BeanUtils.copyProperties(bizModelParam, bizModelDO);

        long count = bizModelDOMapper.updateByPrimaryKeySelective(bizModelDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        Long bizId = bizModelParam.getBizId();

        // 先清空原有
        int delAreaPartnersCount = bizModelRelaAreaPartnersDOMapper.deleteByBizId(bizId);
        int delFinancialProdCount = bizModelRelaFinancialProdDOMapper.deleteByBizId(bizId);

        // 再重新绑定
        List<BizModelParam.RelaAreaIdPartnerIdList> relaAreaIdPartnerIdList = bizModelParam.getRelaAreaIdPartnerIdList();
        List<Long> financialProductIdList = bizModelParam.getFinancialProductIdList();

        bindAreaIdPartnerIdList(bizId, relaAreaIdPartnerIdList);
        bindFinancialProductIdList(bizId, financialProductIdList);

        return ResultBean.ofSuccess(null, "修改成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> delete(Long bizId) {
        Preconditions.checkNotNull(bizId, "bizId");

        BizModelDO bizModelDO = bizModelDOMapper.selectByPrimaryKey(bizId);
        Preconditions.checkNotNull(bizModelDO, "bizId，数据不存在.");

        bizModelDO.setStatus(2);
        long count = bizModelDOMapper.updateByPrimaryKeySelective(bizModelDO);
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> disable(Long bizId) {
        Preconditions.checkNotNull(bizId, "bizId");

        BizModelDO bizModelDO = bizModelDOMapper.selectByPrimaryKey(bizId);
        Preconditions.checkNotNull(bizModelDO, "bizId，数据不存在.");

        bizModelDO.setStatus(1);
        long count = bizModelDOMapper.updateByPrimaryKeySelective(bizModelDO);
        return ResultBean.ofSuccess(null, "停用成功");
    }

    @Override
    @Transactional
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
        if (!CollectionUtils.isEmpty(bizModelRelaAreaPartnersDOList)) {
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
                List<PartnerVO> partnerVOList = partnerService.batchGetById(parterGroupIdList).getData();
                bizModelRegionVO.setPartnerVOList(partnerVOList);
                bizModelRegionVOList.add(bizModelRegionVO);
            }
        }

        List<BizModelRelaFinancialProdDO> bizModelRelaFinancialProdDOList = bizModelRelaFinancialProdService.getById(bizId).getData();
        List<FinancialProductVO> financialProductDOList = Lists.newArrayList();
        for (BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO : bizModelRelaFinancialProdDOList) {
            FinancialProductVO financialProductVO = financialProductService.getById(bizModelRelaFinancialProdDO.getProdId()).getData();
            financialProductDOList.add(financialProductVO);
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
                    if (!CollectionUtils.isEmpty(cityList)) {
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


    /**
     * bind  Area - Partners
     *
     * @param bizId
     * @param relaAreaIdPartnerIdList
     */
    private void bindAreaIdPartnerIdList(Long bizId, List<BizModelParam.RelaAreaIdPartnerIdList> relaAreaIdPartnerIdList) {

        if (!CollectionUtils.isEmpty(relaAreaIdPartnerIdList)) {

            List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList = Lists.newArrayList();
            relaAreaIdPartnerIdList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Long areaId = e.getAreaId();

                        List<Long> partnerIdList = e.getPartnerIdList();
                        if (!CollectionUtils.isEmpty(partnerIdList)) {

                            partnerIdList.stream()
                                    .filter(Objects::nonNull)
                                    .forEach(partnerId -> {
                                        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                                        bizModelRelaAreaPartnersDO.setBizId(bizId);
                                        bizModelRelaAreaPartnersDO.setAreaId(areaId);
                                        bizModelRelaAreaPartnersDO.setGroupId(partnerId);
                                        bizModelRelaAreaPartnersDO.setGmtCreate(new Date());
                                        bizModelRelaAreaPartnersDO.setGmtModify(new Date());
                                        bizModelRelaAreaPartnersDOList.add(bizModelRelaAreaPartnersDO);
                                    });

                        } else {
                            BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                            bizModelRelaAreaPartnersDO.setBizId(bizId);
                            bizModelRelaAreaPartnersDO.setAreaId(areaId);
                            bizModelRelaAreaPartnersDO.setGroupId(NOT_LIMIT_PARTNER);
                            bizModelRelaAreaPartnersDO.setGmtCreate(new Date());
                            bizModelRelaAreaPartnersDO.setGmtModify(new Date());
                            bizModelRelaAreaPartnersDOList.add(bizModelRelaAreaPartnersDO);
                        }

                    });

            // bind  Area - Partners
            ResultBean<Void> bindAreaPartnersResultBean = bizModelRelaAreaPartnersService.batchInsert(bizModelRelaAreaPartnersDOList);
            Preconditions.checkArgument(bindAreaPartnersResultBean.getSuccess(), bindAreaPartnersResultBean.getMsg());
        }
    }

    /**
     * bind  financialProduct
     *
     * @param bizId
     * @param financialProductIdList
     */
    private void bindFinancialProductIdList(Long bizId, List<Long> financialProductIdList) {

        if (!CollectionUtils.isEmpty(financialProductIdList)) {

            List<BizModelRelaFinancialProdDO> bizModelRelaFinancialProdDOList = Lists.newArrayList();

            financialProductIdList.stream()
                    .filter(Objects::nonNull)
                    .forEach(financialProductId -> {

                        BizModelRelaFinancialProdDO bizModelRelaFinancialProdDO = new BizModelRelaFinancialProdDO();
                        bizModelRelaFinancialProdDO.setBizId(bizId);
                        bizModelRelaFinancialProdDO.setProdId(financialProductId);
                        bizModelRelaFinancialProdDO.setGmtCreate(new Date());
                        bizModelRelaFinancialProdDO.setGmtModify(new Date());

                        bizModelRelaFinancialProdDOList.add(bizModelRelaFinancialProdDO);
                    });

            // bind  financialProduct
            ResultBean<Void> bindFinancialProductResultBean = bizModelRelaFinancialProdService.batchInsert(bizModelRelaFinancialProdDOList);
            Preconditions.checkArgument(bindFinancialProductResultBean.getSuccess(), bindFinancialProductResultBean.getMsg());
        }
    }
}
