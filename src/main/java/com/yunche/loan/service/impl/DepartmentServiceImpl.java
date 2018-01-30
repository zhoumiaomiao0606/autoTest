package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.DepartmentQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.DepartmentParam;
import com.yunche.loan.domain.viewObj.DepartmentVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
import com.yunche.loan.service.DepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentDOMapper departmentDOMapper;
    @Autowired
    private DepartmentRelaUserGroupDOMapper departmentRelaUserGroupDOMapper;
    @Autowired
    private UserGroupDOMapper userGroupDOMapper;
    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    @Override
    public ResultBean<Long> create(DepartmentParam departmentParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(departmentParam.getName()), "部门名称不能为空");
//        Preconditions.checkNotNull(departmentParam.getParentId(), "上级部门不能为空");
        Preconditions.checkNotNull(departmentParam.getLeaderId(), "部门负责人不能为空");
        Preconditions.checkNotNull(departmentParam.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(departmentParam.getStatus()) || INVALID_STATUS.equals(departmentParam.getStatus()),
                "状态非法");

        // 创建实体，并返回ID
        Long id = insertAndGetId(departmentParam);

        // 绑定用户组(角色)列表
        bindUserGroup(id, departmentParam.getUserGroupIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        checkHasChilds(id);

        int count = departmentDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> update(DepartmentDO departmentDO) {
        Preconditions.checkNotNull(departmentDO.getId(), "id不能为空");

        // 校验是否是删除操作
        checkIfDel(departmentDO);

        departmentDO.setGmtModify(new Date());
        int count = departmentDOMapper.updateByPrimaryKeySelective(departmentDO);
        Preconditions.checkArgument(count > 0, "编辑失败");
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<DepartmentVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(departmentDO, "id有误，数据不存在");

        DepartmentVO departmentVO = new DepartmentVO();
        BeanUtils.copyProperties(departmentDO, departmentVO);
        // 补充信息
        fillMsg(departmentDO, departmentVO);

        return ResultBean.ofSuccess(departmentVO);
    }

    @Override
    public ResultBean<List<DepartmentVO>> query(DepartmentQuery query) {
        int totalNum = departmentDOMapper.count(query);
        if (totalNum > 0) {

            List<DepartmentDO> departmentDOS = departmentDOMapper.query(query);
            if (!CollectionUtils.isEmpty(departmentDOS)) {

                List<DepartmentVO> departmentVOS = departmentDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            DepartmentVO departmentVO = new DepartmentVO();
                            BeanUtils.copyProperties(e, departmentVO);
                            // 补充信息
                            fillMsg(e, departmentVO);

                            return departmentVO;
                        })
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(departmentVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<LevelVO>> listAll() {
        List<DepartmentDO> departmentDOS = departmentDOMapper.getAll(VALID_STATUS);

        // parentId - DOS
        Map<Long, List<DepartmentDO>> parentIdDOMap = getParentIdDOSMapping(departmentDOS);

        // 分级递归解析
        List<LevelVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        return ResultBean.ofSuccess(topLevelList);
    }

    @Override
    public ResultBean<List<UserGroupVO>> listUserGroup(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "部门ID不能为空");

        int totalNum = userGroupDOMapper.countListUserGroupByDepartmentId(query);
        if (totalNum > 0) {

            List<UserGroupDO> userGroupDOS = userGroupDOMapper.listUserGroupByDepartmentId(query);
            if (!CollectionUtils.isEmpty(userGroupDOS)) {

                List<UserGroupVO> userGroupVOS = userGroupDOS.stream()
                        .filter(Objects::nonNull)
                        .map(userGroupDO -> {

                            UserGroupVO userGroupVO = new UserGroupVO();
                            BeanUtils.copyProperties(userGroupDO, userGroupVO);
                            return userGroupVO;
                        })
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(userGroupVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<Void> bindUserGroup(Long id, String userGroupIds) {
        Preconditions.checkNotNull(id, "部门ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupIds), "用户组ID不能为空");

        List<Long> userGroupIdList = Arrays.asList(userGroupIds.split(",")).stream()
                .map(e -> {
                    return Long.valueOf(e);
                })
                .collect(Collectors.toList());
        bindUserGroup(id, userGroupIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    public ResultBean<Void> unbindUserGroup(Long id, String userGroupIds) {
        Preconditions.checkNotNull(id, "部门ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupIds), "用户组ID不能为空");

        Arrays.asList(userGroupIds.split(",")).stream()
                .distinct()
                .forEach(userGroupId -> {
                    DepartmentRelaUserGroupDOKey departmentRelaUserGroupDOKey = new DepartmentRelaUserGroupDOKey();
                    departmentRelaUserGroupDOKey.setDepartmentId(id);
                    departmentRelaUserGroupDOKey.setUserGroupId(Long.valueOf(userGroupId));
                    int count = departmentRelaUserGroupDOMapper.deleteByPrimaryKey(departmentRelaUserGroupDOKey);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }


    /**
     * parentId - DOS 映射
     *
     * @param departmentDOS
     * @return
     */
    private Map<Long, List<DepartmentDO>> getParentIdDOSMapping(List<DepartmentDO> departmentDOS) {
        if (CollectionUtils.isEmpty(departmentDOS)) {
            return null;
        }

        Map<Long, List<DepartmentDO>> parentIdDOMap = Maps.newHashMap();
        departmentDOS.stream()
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
    private List<LevelVO> parseLevelByLevel(Map<Long, List<DepartmentDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<DepartmentDO> parentDOS = parentIdDOMap.get(-1L);
            if (!CollectionUtils.isEmpty(parentDOS)) {
                List<LevelVO> topLevelList = parentDOS.stream()
                        .map(p -> {
                            LevelVO parent = new LevelVO();
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
    private void fillChilds(LevelVO parent, Map<Long, List<DepartmentDO>> parentIdDOMap) {
        List<DepartmentDO> childs = parentIdDOMap.get(parent.getValue());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

        childs.stream()
                .forEach(c -> {
                    LevelVO child = new LevelVO();
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

    /**
     * 插入实体，并返回主键ID
     *
     * @param departmentParam
     * @return
     */
    private Long insertAndGetId(DepartmentParam departmentParam) {
        List<String> nameList = departmentDOMapper.getAllName(VALID_STATUS);
        Preconditions.checkArgument(!nameList.contains(departmentParam.getName().trim()), "部门名称已存在");

        DepartmentDO departmentDO = new DepartmentDO();
        BeanUtils.copyProperties(departmentParam, departmentDO);
        // level
        Long parentId = departmentParam.getParentId();
        if (null != parentId) {
            DepartmentDO parentDepartmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            Preconditions.checkNotNull(parentDepartmentDO, "上级部门不存在");
            Integer parentLevel = parentDepartmentDO.getLevel();
            Integer level = parentLevel == null ? null : parentLevel + 1;
            departmentDO.setLevel(level);
        } else {
            departmentDO.setLevel(1);
        }
        // date
        departmentDO.setGmtCreate(new Date());
        departmentDO.setGmtModify(new Date());

        int count = departmentDOMapper.insertSelective(departmentDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return departmentDO.getId();
    }

    /**
     * 绑定用户组(角色)列表
     *
     * @param departmentId
     * @param userGroupIdList
     */
    private void bindUserGroup(Long departmentId, List<Long> userGroupIdList) {
        if (CollectionUtils.isEmpty(userGroupIdList)) {
            return;
        }

        // 去重
        List<Long> existUserGroupIdList = departmentRelaUserGroupDOMapper.getUserGroupIdListByDepartmentId(departmentId);
        if (!CollectionUtils.isEmpty(existUserGroupIdList)) {

            userGroupIdList = userGroupIdList.parallelStream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(e -> {
                        if (!existUserGroupIdList.contains(e)) {
                            return e;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 绑定
        if (!CollectionUtils.isEmpty(userGroupIdList)) {
            List<DepartmentRelaUserGroupDO> departmentRelaUserGroupDOS = userGroupIdList.parallelStream()
                    .map(e -> {
                        DepartmentRelaUserGroupDO departmentRelaUserGroupDO = new DepartmentRelaUserGroupDO();
                        departmentRelaUserGroupDO.setDepartmentId(departmentId);
                        departmentRelaUserGroupDO.setUserGroupId(e);
                        departmentRelaUserGroupDO.setGmtCreate(new Date());
                        departmentRelaUserGroupDO.setGmtModify(new Date());

                        return departmentRelaUserGroupDO;
                    })
                    .collect(Collectors.toList());

            int count = departmentRelaUserGroupDOMapper.batchInsert(departmentRelaUserGroupDOS);
            Preconditions.checkArgument(count == departmentRelaUserGroupDOS.size(), "关联业务范围失败");
        }
    }

    /**
     * 校验是否是删除操作
     *
     * @param departmentDO
     */
    private void checkIfDel(DepartmentDO departmentDO) {
        if (INVALID_STATUS.equals(departmentDO.getStatus())) {
            // 校验是否存在子部门
            checkHasChilds(departmentDO.getId());
        }
    }

    /**
     * 校验是否存在子部门
     *
     * @param parentId
     */
    private void checkHasChilds(Long parentId) {
        List<DepartmentDO> departmentDOS = departmentDOMapper.getByParentId(parentId, VALID_STATUS);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(departmentDOS), "请先删除所有下级部门");
    }

    /**
     * 补充对象信息
     * id-name
     *
     * @param departmentDO
     * @param departmentVO
     */
    private void fillMsg(DepartmentDO departmentDO, DepartmentVO departmentVO) {
        fillParent(departmentDO.getParentId(), departmentVO);
        fillLeader(departmentDO.getLeaderId(), departmentVO);
        fillArea(departmentDO.getAreaId(), departmentVO);
        // 填充部门人数
        fillNum(departmentVO);
    }

    /**
     * 填充parent
     *
     * @param parentId
     * @param departmentVO
     */
    private void fillParent(Long parentId, DepartmentVO departmentVO) {
        if (null == parentId) {
            return;
        }
        DepartmentDO parentDepartmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
        if (null != parentDepartmentDO) {
            // 递归填充所有上层父级部门
            fillSupperDepartmentId(parentDepartmentDO.getParentId(), Lists.newArrayList(parentDepartmentDO.getId()), departmentVO);
        }
    }

    private void fillSupperDepartmentId(Long parentId, List<Long> parent, DepartmentVO departmentVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                parent.add(parentId);
                fillSupperDepartmentId(departmentDO.getParentId(), parent, departmentVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(parent);
            departmentVO.setParent(parent);
        }
    }

    private void fillLeader(Long employeeId, DepartmentVO departmentVO) {
        if (null == employeeId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(employeeId, VALID_STATUS);
        if (null != employeeDO) {
            // 递归填充所有上层父级leader
            fillSupperLeaderId(employeeDO.getParentId(), Lists.newArrayList(employeeDO.getId()), departmentVO);
        }
    }

    private void fillSupperLeaderId(Long parentId, List<Long> leader, DepartmentVO departmentVO) {
        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                leader.add(parentId);
                fillSupperLeaderId(employeeDO.getParentId(), leader, departmentVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(leader);
            departmentVO.setLeader(leader);
        }
    }


    private void fillArea(Long areaId, DepartmentVO departmentVO) {
        if (null == areaId) {
            return;
        }
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {
            // 递归填充所有上层父级部门
            fillSupperAreaId(baseAreaDO.getParentAreaId(), Lists.newArrayList(baseAreaDO.getAreaId()), departmentVO);
        }
    }

    /**
     * @param parentId
     * @param area
     * @param departmentVO
     */
    private void fillSupperAreaId(Long parentId, List<Long> area, DepartmentVO departmentVO) {
        if (null != parentId) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != baseAreaDO) {
                area.add(parentId);
                fillSupperAreaId(baseAreaDO.getParentAreaId(), area, departmentVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(area);
            departmentVO.setArea(area);
        }
    }


    /**
     * todo 填充部门人数
     *
     * @param departmentVO
     */
    private void fillNum(DepartmentVO departmentVO) {

        departmentVO.setEmployeeNum(10);
    }
}
