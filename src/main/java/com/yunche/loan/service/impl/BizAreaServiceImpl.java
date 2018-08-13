package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.domain.query.BizAreaQuery;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.BizAreaParam;
import com.yunche.loan.service.BizAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;
import static com.yunche.loan.config.constant.AreaConst.LEVEL_PROV;
import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Service
@Transactional
public class BizAreaServiceImpl implements BizAreaService {

    @Autowired
    private BizAreaDOMapper bizAreaDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private BizAreaRelaAreaDOMapper bizAreaRelaAreaDOMapper;

    @Resource
    private BizAreaRelaPartnerDOMapper bizAreaRelaPartnerDOMapper;


    @Override
    public ResultBean<Long> create(BizAreaParam bizAreaParam) {
        Preconditions.checkArgument(null != bizAreaParam && StringUtils.isNotBlank(bizAreaParam.getName()), "名称不能为空");
        Preconditions.checkNotNull(bizAreaParam.getEmployeeId(), "部门负责人不能为空");
        Preconditions.checkNotNull(bizAreaParam.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(bizAreaParam.getStatus()) || INVALID_STATUS.equals(bizAreaParam.getStatus()),
                "状态非法");

        // 名称已存在校验
        List<String> nameList = bizAreaDOMapper.getAllName(VALID_STATUS);
        Preconditions.checkArgument(!nameList.contains(bizAreaParam.getName().trim()), "名称已存在");

        // insert
        Long id = insertAndGetId(bizAreaParam);

        // 绑定城市列表  
        //bindAreas(id, bizAreaParam.getAreaIdList());

        bindPartner(id, bizAreaParam.getPartnerIds());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        // 校验是否存在子级区域
        checkHasChilds(id);

        // del
        int count = bizAreaDOMapper.deleteByPrimaryKey(id);
        bizAreaRelaPartnerDOMapper.deleteByBizAreaId(id);

        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> update(BizAreaDO bizAreaDO) {
        Preconditions.checkArgument(null != bizAreaDO && null != bizAreaDO.getId(), "id不能为空");
        Preconditions.checkArgument(!bizAreaDO.getId().equals(bizAreaDO.getParentId()), "上级区域不能为自身");

        // 校验是否是删除操作
        checkIfDel(bizAreaDO);

        // level
        Long parentId = bizAreaDO.getParentId();
        if (null != parentId) {
            BizAreaDO parentBizAreaDO = bizAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != parentBizAreaDO) {
                Integer parentLevel = parentBizAreaDO.getLevel();
                Integer level = parentLevel == null ? null : parentLevel + 1;
                bizAreaDO.setLevel(level);
            }
        }

        // update
        bizAreaDO.setGmtModify(new Date());
        int count = bizAreaDOMapper.updateByPrimaryKeySelective(bizAreaDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<BizAreaVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        BizAreaDO bizAreaDO = bizAreaDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(bizAreaDO, "id有误，数据不存在.");

        BizAreaVO bizAreaVO = new BizAreaVO();
        BeanUtils.copyProperties(bizAreaDO, bizAreaVO);

        // 补充上级业务区域
        fillParent(bizAreaDO.getParentId(), bizAreaVO);
        // 补充负责人
        fillLeader(bizAreaDO.getEmployeeId(), bizAreaVO);

        return ResultBean.ofSuccess(bizAreaVO);
    }

    @Override
    public ResultBean<List<BizAreaVO>> query(BizAreaQuery query) {
        int totalNum = bizAreaDOMapper.count(query);
        if (totalNum > 0) {

            List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.query(query);
            if (!CollectionUtils.isEmpty(bizAreaDOS)) {

                List<BizAreaVO> bizAreaVOS = bizAreaDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            BizAreaVO bizAreaVO = new BizAreaVO();
                            BeanUtils.copyProperties(e, bizAreaVO);

                            // 补充上级业务区域
                            fillParent(e.getParentId(), bizAreaVO);
                            // 补充负责人
                            fillLeader(e.getEmployeeId(), bizAreaVO);

                            return bizAreaVO;
                        })
                        .collect(Collectors.toList());
                return ResultBean.ofSuccess(bizAreaVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<CascadeAreaVO.Prov>> listArea(BizAreaQuery query) {
        // 业务区域ID不能为空
        Preconditions.checkNotNull(query.getId(), "id不能为空");

        int totalNum = bizAreaRelaAreaDOMapper.count(query);
        if (totalNum > 0) {

            List<BizAreaRelaAreaDO> bizAreaRelaAreaDOS = bizAreaRelaAreaDOMapper.query(query);
            if (!CollectionUtils.isEmpty(bizAreaRelaAreaDOS)) {

                List<CascadeAreaVO.Prov> bizAreaVOS = bizAreaRelaAreaDOS.stream()
                        .filter(e -> null != e && null != e.getAreaId())
                        .map(e -> {

                            // 关联的区域
                            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(e.getAreaId(), VALID_STATUS);
                            if (null != baseAreaDO) {

                                CascadeAreaVO.Prov province = new CascadeAreaVO.Prov();
                                // 关联区域的等级
                                Byte level = baseAreaDO.getLevel();

                                // 关联到市 ：补充省
                                if (LEVEL_CITY.equals(level)) {

                                    // 填充市
                                    CascadeAreaVO.City city = new CascadeAreaVO.City();
                                    city.setId(baseAreaDO.getAreaId());
                                    city.setName(baseAreaDO.getAreaName());
                                    city.setLevel(level);
                                    province.setCity(city);

                                    // 补充省
                                    BaseAreaDO parentBaseAreaDO = baseAreaDOMapper.selectByPrimaryKey(baseAreaDO.getParentAreaId(), VALID_STATUS);
                                    if (null != parentBaseAreaDO) {
                                        province.setId(parentBaseAreaDO.getAreaId());
                                        province.setName(parentBaseAreaDO.getAreaName());
                                        province.setLevel(parentBaseAreaDO.getLevel());
                                    }
                                    return province;

                                    // 关联到省
                                } else if (LEVEL_PROV.equals(level)) {
                                    // 填充省
                                    province.setId(baseAreaDO.getAreaId());
                                    province.setName(baseAreaDO.getAreaName());
                                    province.setLevel(baseAreaDO.getLevel());
                                    return province;
                                }

                            }

                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(bizAreaVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }

        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<Void> bindArea(Long id, String areaIds) {
        Preconditions.checkNotNull(id, "业务区域ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(areaIds), "城市ID不能为空");

        List<Long> areaIdList = Arrays.asList(areaIds.split(",")).stream()
                .map(e -> {
                    return Long.valueOf(e);
                })
                .collect(Collectors.toList());
        bindAreas(id, areaIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    public ResultBean<Void> unbindArea(Long id, String areaIds) {
        Preconditions.checkNotNull(id, "业务区域ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(areaIds), "城市ID不能为空");

        Arrays.asList(areaIds.split(",")).stream()
                .distinct()
                .forEach(areaId -> {
                    BizAreaRelaAreaDOKey bizAreaRelaAreaDOKey = new BizAreaRelaAreaDOKey();
                    bizAreaRelaAreaDOKey.setBizAreaId(id);
                    bizAreaRelaAreaDOKey.setAreaId(Long.valueOf(areaId));
                    int count = bizAreaRelaAreaDOMapper.deleteByPrimaryKey(bizAreaRelaAreaDOKey);
                    Preconditions.checkArgument(count > 0, "删除失败");
                });

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<List<CascadeAreaVO.Partner>> listPartner(Long id) {
        List<CascadeAreaVO.Partner> result = Lists.newArrayList();
        List<BizAreaPartnerVO> list = bizAreaRelaPartnerDOMapper.selectByAreaId(id);
        for (BizAreaPartnerVO bizAreaPartnerVO : list) {
            if (bizAreaPartnerVO != null) {
                CascadeAreaVO.Partner partner = new CascadeAreaVO.Partner();
                partner.setPartnerId(StringUtils.isBlank(bizAreaPartnerVO.getPartner_id()) ? null : Long.valueOf(bizAreaPartnerVO.getPartner_id()));
                partner.setPartnerName(bizAreaPartnerVO.getPartner_name());
                result.add(partner);
            }
        }
        return ResultBean.ofSuccess(result);
    }

    @Override
    public ResultBean<Void> bindPartner(Long id, List<Long> partnerIds) {
        if (id == null) {
            throw new BizException("缺少大区ID");
        }
        BizAreaDO bizAreaDO = bizAreaDOMapper.selectByPrimaryKey(id, new Byte("0"));
        if (bizAreaDO == null) {
            throw new BizException("大区不存在");
        }


        bizAreaRelaPartnerDOMapper.deleteByBizAreaId(id);

        for (Long partnerId : partnerIds) {
            if (partnerId != null) {
                BizAreaRelaPartnerDOKey key = new BizAreaRelaPartnerDOKey();
                key.setPartnerId(partnerId);
                key.setBizAreaId(id);
                BizAreaRelaPartnerDO DO = bizAreaRelaPartnerDOMapper.selectByPrimaryKey(key);
                if (DO == null) {
                    BizAreaRelaPartnerDO bizAreaRelaPartnerDO = new BizAreaRelaPartnerDO();
                    bizAreaRelaPartnerDO.setBizAreaId(id);
                    bizAreaRelaPartnerDO.setPartnerId(partnerId);
                    bizAreaRelaPartnerDOMapper.insertSelective(bizAreaRelaPartnerDO);
                } else {
                    BizAreaRelaPartnerDO bizAreaRelaPartnerDO = new BizAreaRelaPartnerDO();
                    bizAreaRelaPartnerDO.setBizAreaId(id);
                    bizAreaRelaPartnerDO.setPartnerId(partnerId);
                    bizAreaRelaPartnerDOMapper.updateByPrimaryKey(bizAreaRelaPartnerDO);
                }
            }
        }
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> unbindPartner(Long id, Long partnerId) {

        if (id == null) {
            throw new BizException("缺少大区ID");
        }
        BizAreaDO bizAreaDO = bizAreaDOMapper.selectByPrimaryKey(id, new Byte("0"));
        if (bizAreaDO == null) {
            throw new BizException("大区不存在");
        }

        if (partnerId == null) {
            throw new BizException("缺少合伙人ID");
        }

        BizAreaRelaPartnerDOKey key = new BizAreaRelaPartnerDOKey();
        key.setBizAreaId(id);
        key.setPartnerId(partnerId);
        bizAreaRelaPartnerDOMapper.deleteByPrimaryKey(key);

        return ResultBean.ofSuccess(null, "编辑成功");
    }


    @Override
    public ResultBean<List<CascadeVO>> listAll() {

        List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.getAll(VALID_STATUS);
//        Preconditions.checkArgument(!CollectionUtils.isEmpty(bizAreaDOS), "无有效业务区域数据");

        // parentId - DOS
        Map<Long, List<BizAreaDO>> parentIdDOMap = getParentIdDOSMapping(bizAreaDOS);

        // 分级递归解析
        List<CascadeVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        return ResultBean.ofSuccess(topLevelList);
    }

    //根据选择对区域回显
    public List<List<Long>> selectedList(List<Long> bizAreaIds) {
        if (bizAreaIds == null || CollectionUtils.isEmpty(bizAreaIds)) {
            return Lists.newArrayList();
        }

        if (bizAreaIds.size() == 0) {
            return Lists.newArrayList();
        }

        List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.getAll(VALID_STATUS);
//        Preconditions.checkArgument(!CollectionUtils.isEmpty(bizAreaDOS), "无有效业务区域数据");
        List<List<Long>> reuslt = Lists.newLinkedList();
        for (Long str : bizAreaIds) {
            List<Long> supper = getAllSuperAreaIdList(str, bizAreaDOS);
            Collections.reverse(supper);
            supper.add(str);
            reuslt.add(supper);
        }

        return reuslt;
    }

    private List<Long> getAllSuperAreaIdList(Long childBizAreaId, List<BizAreaDO> list) {
        List<Long> allSuperAreaIdList = Lists.newLinkedList();
        list.parallelStream().filter(e -> null != e && childBizAreaId.equals(e.getId()) && null != e.getParentId())
                .forEach(e -> {
                    Long cAreaId = e.getParentId();
                    // 递归调用
                    List<Long> superAreaIdList = getAllSuperAreaIdList(cAreaId, list);
                    superAreaIdList.add(cAreaId);
                    allSuperAreaIdList.addAll(superAreaIdList);
                });
        return allSuperAreaIdList;
    }

    /**
     * 创建实体，并返回ID
     *
     * @param bizAreaParam
     * @return
     */
    private Long insertAndGetId(BizAreaParam bizAreaParam) {
        BizAreaDO bizAreaDO = new BizAreaDO();
        BeanUtils.copyProperties(bizAreaParam, bizAreaDO);

        // level
        Long parentId = bizAreaDO.getParentId();
        if (null == parentId) {
            bizAreaDO.setLevel(1);
        } else {
            BizAreaDO parentBizAreaDO = bizAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            Preconditions.checkNotNull(parentBizAreaDO, "上一级区域不存在");
            bizAreaDO.setLevel(parentBizAreaDO.getLevel() + 1);
        }

        // date
        bizAreaDO.setGmtCreate(new Date());
        bizAreaDO.setGmtModify(new Date());

        int count = bizAreaDOMapper.insertSelective(bizAreaDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return bizAreaDO.getId();
    }

    /**
     * 绑定城市列表
     *
     * @param bizAreaId
     * @param areaIdList
     */
    private void bindAreas(Long bizAreaId, List<Long> areaIdList) {
        if (CollectionUtils.isEmpty(areaIdList)) {
            return;
        }

        // 去重（包含已绑定过的）
        List<Long> existAreaIdList = bizAreaRelaAreaDOMapper.getAreaIdListByBizAreaId(bizAreaId);
        if (!CollectionUtils.isEmpty(existAreaIdList)) {

            areaIdList = areaIdList.parallelStream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(areaId -> {
                        if (!existAreaIdList.contains(areaId)) {
                            return areaId;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }

        // 绑定
        if (!CollectionUtils.isEmpty(areaIdList)) {
            List<BizAreaRelaAreaDO> bizAreaRelaAreaDOS = areaIdList.parallelStream()
                    .map(areaId -> {
                        BizAreaRelaAreaDO bizAreaRelaAreaDO = new BizAreaRelaAreaDO();
                        bizAreaRelaAreaDO.setAreaId(areaId);
                        bizAreaRelaAreaDO.setBizAreaId(bizAreaId);
                        bizAreaRelaAreaDO.setGmtCreate(new Date());
                        bizAreaRelaAreaDO.setGmtModify(new Date());

                        return bizAreaRelaAreaDO;
                    })
                    .collect(Collectors.toList());

            int areaCount = bizAreaRelaAreaDOMapper.batchInsert(bizAreaRelaAreaDOS);
            Preconditions.checkArgument(areaCount == bizAreaRelaAreaDOS.size(), "关联业务范围失败");
        }
    }

    /**
     * 如果是做删除操作
     *
     * @param bizAreaDO
     */
    private void checkIfDel(BizAreaDO bizAreaDO) {
        if (INVALID_STATUS.equals(bizAreaDO.getStatus())) {
            // 校验是否存在子级区域
            checkHasChilds(bizAreaDO.getId());
        }
    }

    /**
     * 校验是否存在子级区域
     *
     * @param parentId
     */
    private void checkHasChilds(Long parentId) {
        List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.getByParentId(parentId, VALID_STATUS);
        Preconditions.checkArgument(CollectionUtils.isEmpty(bizAreaDOS), "请先删除所有下级区域");
    }

    /**
     * 补充上级业务区域
     *
     * @param parentId
     * @param bizAreaVO
     */
    private void fillParent(Long parentId, BizAreaVO bizAreaVO) {
        if (null != parentId) {
            BizAreaDO bizAreaDO = bizAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != bizAreaDO) {
                BaseVO parentBizArea = new BaseVO();
                BeanUtils.copyProperties(bizAreaDO, parentBizArea);
                // 递归填充父级业务区域
                fillSuperBizArea(bizAreaDO.getParentId(), Lists.newArrayList(parentBizArea), bizAreaVO);
            }
        }
    }

    /**
     * 递归填充父级业务区域
     *
     * @param parentId
     * @param superBizAreaList
     * @param bizAreaVO
     */
    private void fillSuperBizArea(Long parentId, List<BaseVO> superBizAreaList, BizAreaVO bizAreaVO) {
        if (null != parentId) {
            BizAreaDO bizAreaDO = bizAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != bizAreaDO) {
                BaseVO parentBizArea = new BaseVO();
                BeanUtils.copyProperties(bizAreaDO, parentBizArea);
                superBizAreaList.add(parentBizArea);
                fillSuperBizArea(bizAreaDO.getParentId(), superBizAreaList, bizAreaVO);
            }
        } else {
            Collections.reverse(superBizAreaList);
            bizAreaVO.setParent(superBizAreaList);
        }
    }

    /**
     * 补充负责人
     *
     * @param employeeId
     * @param bizAreaVO
     */
    private void fillLeader(Long employeeId, BizAreaVO bizAreaVO) {
        if (null != employeeId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(employeeId, VALID_STATUS);
            if (null != employeeDO) {
                BaseVO parentEmployee = new BaseVO();
                BeanUtils.copyProperties(employeeDO, parentEmployee);
                // 递归填充父级负责人
                fillSuperLeader(employeeDO.getParentId(), Lists.newArrayList(parentEmployee), bizAreaVO, 50);
            }
        }
    }

    /**
     * 递归填充父级负责人
     *
     * @param parentId
     * @param superLeaderList
     * @param bizAreaVO
     * @param limit
     */
    private void fillSuperLeader(Long parentId, List<BaseVO> superLeaderList, BizAreaVO bizAreaVO, Integer limit) {

        limit--;
        if (limit < 0) {
            return;
        }

        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                BaseVO parentEmployee = new BaseVO();
                BeanUtils.copyProperties(employeeDO, parentEmployee);
                superLeaderList.add(parentEmployee);
                fillSuperLeader(employeeDO.getParentId(), superLeaderList, bizAreaVO, limit);
            }
        } else {
            Collections.reverse(superLeaderList);
            bizAreaVO.setLeader(superLeaderList);
        }
    }

    /**
     * parentId - DOS 映射
     *
     * @param bizAreaDOS
     * @return
     */
    private Map<Long, List<BizAreaDO>> getParentIdDOSMapping(List<BizAreaDO> bizAreaDOS) {
        if (CollectionUtils.isEmpty(bizAreaDOS)) {
            return null;
        }

        Map<Long, List<BizAreaDO>> parentIdDOMap = Maps.newConcurrentMap();
        bizAreaDOS.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Long parentId = e.getParentId();
                    // 为null,用-1标记
                    parentId = null == parentId ? -1L : parentId;
                    if (!parentIdDOMap.containsKey(parentId)) {
                        parentIdDOMap.put(parentId, Lists.newArrayList(e));
                    } else {
                        parentIdDOMap.get(parentId).add(e);
                    }

                });

        return parentIdDOMap;
    }


    /**
     * 分级递归解析
     *
     * @param parentIdDOMap
     * @return
     */
    private List<CascadeVO> parseLevelByLevel(Map<Long, List<BizAreaDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<BizAreaDO> parentBizAreaDOS = parentIdDOMap.get(-1L);
            if (!CollectionUtils.isEmpty(parentBizAreaDOS)) {
                List<CascadeVO> topLevelList = parentBizAreaDOS.stream()
                        .map(p -> {
                            CascadeVO parent = new CascadeVO();
                            parent.setValue(p.getId());
                            parent.setLabel(p.getName());
                            parent.setLevel(p.getLevel());

                            // 递归填充子列表
                            fillChilds(parent, parentIdDOMap);
                            return parent;
                        })
                        .collect(Collectors.toList());

                return topLevelList;

            }
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 递归填充子列表
     *
     * @param parent
     * @param parentIdDOMap
     */
    private void fillChilds(CascadeVO parent, Map<Long, List<BizAreaDO>> parentIdDOMap) {
        List<BizAreaDO> childs = parentIdDOMap.get(parent.getValue());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

        childs.stream()
                .forEach(c -> {
                    CascadeVO child = new CascadeVO();
                    child.setValue(c.getId());
                    child.setLabel(c.getName());
                    child.setLevel(c.getLevel());
                    child.setParentId(c.getParentId());
                    List<CascadeVO> childList = parent.getChildren();
                    if (CollectionUtils.isEmpty(childList)) {
                        parent.setChildren(Lists.newArrayList(child));
                    } else {
                        parent.getChildren().add(child);
                    }

                    // 递归填充子列表
                    fillChilds(child, parentIdDOMap);
                });
    }

}
