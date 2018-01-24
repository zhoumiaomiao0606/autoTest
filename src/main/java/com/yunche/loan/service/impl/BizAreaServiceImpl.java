package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BaseAreaDOMapper;
import com.yunche.loan.dao.mapper.BizAreaDOMapper;
import com.yunche.loan.dao.mapper.BizAreaRelaAreaDOMapper;
import com.yunche.loan.dao.mapper.EmployeeDOMapper;
import com.yunche.loan.domain.QueryObj.BizAreaQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.BizAreaParam;
import com.yunche.loan.domain.viewObj.AreaVO;
import com.yunche.loan.domain.viewObj.BizAreaVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.service.BizAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;
import static com.yunche.loan.config.constant.AreaConst.LEVEL_PROV;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Service
@Transactional(rollbackFor = BizException.class)
public class BizAreaServiceImpl implements BizAreaService {

    @Autowired
    private BizAreaDOMapper bizAreaDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private BizAreaRelaAreaDOMapper bizAreaRelaAreaDOMapper;


    @Override
    public ResultBean<Long> create(BizAreaParam bizAreaParam) {
        Preconditions.checkArgument(null != bizAreaParam && StringUtils.isNotBlank(bizAreaParam.getName()), "名称不能为空");
        Preconditions.checkNotNull(bizAreaParam.getEmployeeId(), "部门负责人不能为空");

        // 名称已存在校验
        List<String> nameList = bizAreaDOMapper.getAllName(VALID_STATUS);
        Preconditions.checkArgument(!nameList.contains(bizAreaParam.getName().trim()), "名称已存在");

        // insert
        Long id = insertAndGetId(bizAreaParam);

        // 绑定城市列表  
        bindAreas(id, bizAreaParam.getAreaIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        // 校验是否存在子级区域
        checkHasChilds(id);

        int count = bizAreaDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> update(BizAreaDO bizAreaDO) {
        Preconditions.checkArgument(null != bizAreaDO && null != bizAreaDO.getId(), "id不能为空");

        // update
        bizAreaDO.setGmtModify(new Date());
        int count = bizAreaDOMapper.updateByPrimaryKeySelective(bizAreaDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<BizAreaVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        BizAreaDO bizAreaDO = bizAreaDOMapper.selectByPrimaryKey(id, VALID_STATUS);
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
        if (totalNum < 1) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.query(query);
        if (CollectionUtils.isEmpty(bizAreaDOS)) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<BizAreaVO> bizAreaVOS = bizAreaDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    BizAreaVO bizAreaVO = new BizAreaVO();
                    BeanUtils.copyProperties(e, bizAreaVO);
                    return bizAreaVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(bizAreaVOS, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<AreaVO.Prov>> listCity(BizAreaQuery query) {
        // 业务区域ID不能为空
        Preconditions.checkNotNull(query.getId(), "id不能为空");

        int totalNum = bizAreaRelaAreaDOMapper.count(query);
        Preconditions.checkArgument(totalNum > 0, "无符合条件的数据");

        List<BizAreaRelaAreaDO> bizAreaRelaAreaDOS = bizAreaRelaAreaDOMapper.query(query);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(bizAreaRelaAreaDOS), "无符合条件的数据");

        List<AreaVO.Prov> bizAreaVOS = bizAreaRelaAreaDOS.stream()
                .filter(e -> null != e && null != e.getAreaId())
                .map(e -> {

                    // 关联的区域
                    BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(e.getAreaId(), VALID_STATUS);
                    if (null != baseAreaDO) {

                        AreaVO.Prov province = new AreaVO.Prov();
                        // 关联区域的等级
                        Byte level = baseAreaDO.getLevel();

                        // 关联到市 ：补充省
                        if (LEVEL_CITY.equals(level)) {

                            // 填充市
                            AreaVO.City city = new AreaVO.City();
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

    @Override
    public ResultBean<Void> deleteRelaArea(Long id, Long areaId) {
        Preconditions.checkNotNull(id, "业务区域ID不能为空");
        Preconditions.checkNotNull(areaId, "城市ID不能为空");

        BizAreaRelaAreaDOKey bizAreaRelaAreaDOKey = new BizAreaRelaAreaDOKey();
        bizAreaRelaAreaDOKey.setBizAreaId(id);
        bizAreaRelaAreaDOKey.setAreaId(areaId);
        int count = bizAreaRelaAreaDOMapper.deleteByPrimaryKey(bizAreaRelaAreaDOKey);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<List<BizAreaVO.Level>> listAll() {

        List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.getAll(VALID_STATUS);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(bizAreaDOS), "无有效业务区域数据");

        // parentId - DOS
        Map<Long, List<BizAreaDO>> parentIdDOMap = getParentIdDOSMapping(bizAreaDOS);

        // 分级递归解析
        List<BizAreaVO.Level> topLevelList = parseLevelByLevel(parentIdDOMap);

        return ResultBean.ofSuccess(topLevelList);
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

        // status
        bizAreaDO.setStatus(VALID_STATUS);
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
                BizAreaVO.Parent parent = new BizAreaVO.Parent();
                BeanUtils.copyProperties(bizAreaDO, parent);
                bizAreaVO.setParent(parent);
            }
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
                BizAreaVO.Leader leader = new BizAreaVO.Leader();
                BeanUtils.copyProperties(employeeDO, leader);
                bizAreaVO.setLeader(leader);
            }
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
    private List<BizAreaVO.Level> parseLevelByLevel(Map<Long, List<BizAreaDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<BizAreaDO> parentBizAreaDOS = parentIdDOMap.get(-1L);
            if (!CollectionUtils.isEmpty(parentBizAreaDOS)) {
                List<BizAreaVO.Level> topLevelList = parentBizAreaDOS.stream()
                        .map(p -> {
                            BizAreaVO.Level parent = new BizAreaVO.Level();
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
    private void fillChilds(LevelVO parent, Map<Long, List<BizAreaDO>> parentIdDOMap) {
        List<BizAreaDO> childs = parentIdDOMap.get(parent.getValue());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

        childs.stream()
                .forEach(c -> {
                    BizAreaVO.Level child = new BizAreaVO.Level();
                    child.setValue(c.getId());
                    child.setLabel(c.getName());
                    child.setLevel(c.getLevel());

                    List<LevelVO> childList = parent.getChildren();
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
