package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.queryObj.BaseQuery;
import com.yunche.loan.domain.queryObj.EmployeeQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.viewObj.BaseVO;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
import com.yunche.loan.service.EmployeeService;
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
import static com.yunche.loan.config.constant.EmployeeConst.TYPE_WB;
import static com.yunche.loan.config.constant.EmployeeConst.TYPE_ZS;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private UserGroupDOMapper userGroupDOMapper;
    @Autowired
    private DepartmentDOMapper departmentDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;
    @Autowired
    private UserGroupRelaAreaAuthDOMapper userGroupRelaAreaAuthDOMapper;
    @Autowired
    private UserGroupRelaAreaDOMapper userGroupRelaAreaDOMapper;


    @Override
    public ResultBean<Long> create(EmployeeParam employeeParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getName()), "姓名不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getIdCard()), "身份证号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getMobile()), "手机号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getEmail()), "电子邮箱不能为空");
        Preconditions.checkNotNull(employeeParam.getStatus(), "员工状态不能为空");
        Preconditions.checkNotNull(employeeParam.getType(), "员工类型不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(employeeParam.getStatus()) || INVALID_STATUS.equals(employeeParam.getStatus()),
                "员工状态非法");
        Preconditions.checkArgument(TYPE_ZS.equals(employeeParam.getType()) || TYPE_WB.equals(employeeParam.getType()),
                "员工类型非法");

        // 创建实体，并返回ID
        Long id = insertAndGetId(employeeParam);

        // 绑定用户组(角色)列表
        bindUserGroup(id, employeeParam.getUserGroupIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> update(EmployeeDO employeeDO) {
        Preconditions.checkNotNull(employeeDO.getId(), "id不能为空");

        employeeDO.setGmtModify(new Date());
        int count = employeeDOMapper.updateByPrimaryKeySelective(employeeDO);
        Preconditions.checkArgument(count > 0, "编辑失败");
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = employeeDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<EmployeeVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(employeeDO, "id有误，数据不存在");

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employeeDO, employeeVO);

        // 填充直接上级信息
        fillParent(employeeDO.getParentId(), employeeVO);
        // 填充所属部门信息
        fillDepartment(employeeDO.getDepartmentId(), employeeVO);

        return ResultBean.ofSuccess(employeeVO);
    }

    @Override
    public ResultBean<List<EmployeeVO>> query(EmployeeQuery query) {
        int totalNum = employeeDOMapper.count(query);
        if (totalNum > 0) {
            List<EmployeeDO> employeeDOS = employeeDOMapper.query(query);
            if (!CollectionUtils.isEmpty(employeeDOS)) {
                List<EmployeeVO> employeeVOS = employeeDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            EmployeeVO employeeVO = new EmployeeVO();
                            BeanUtils.copyProperties(e, employeeVO);

                            // 填充直接上级信息
                            fillParent(e.getParentId(), employeeVO);
                            // 填充所属部门信息
                            fillDepartment(e.getDepartmentId(), employeeVO);

                            return employeeVO;
                        })
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(employeeVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<LevelVO>> listAll() {
        List<EmployeeDO> employeeDOS = employeeDOMapper.getAll(VALID_STATUS);

        // parentId - DOS
        Map<Long, List<EmployeeDO>> parentIdDOMap = getParentIdDOSMapping(employeeDOS);

        // 分级递归解析
        List<LevelVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        return ResultBean.ofSuccess(topLevelList);
    }

    @Override
    public ResultBean<List<String>> listTitle() {
        List<String> listTitle = employeeDOMapper.listTitle();
        listTitle.removeAll(Collections.singleton(null));
        return ResultBean.ofSuccess(listTitle);
    }

    @Override
    public ResultBean<List<UserGroupVO>> listUserGroup(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "员工ID不能为空");

        int totalNum = userGroupDOMapper.countListUserGroupByEmployeeId(query);
        if (totalNum > 0) {

            List<UserGroupDO> userGroupDOS = userGroupDOMapper.listUserGroupByEmployeeId(query);
            if (!CollectionUtils.isEmpty(userGroupDOS)) {

                List<UserGroupVO> userGroupVOList = userGroupDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .map(userGroupDO -> {

                            UserGroupVO userGroupVO = new UserGroupVO();
                            BeanUtils.copyProperties(userGroupDO, userGroupVO);

                            fillDepartment(userGroupDO.getDepartmentId(), userGroupVO);
                            fillArea(userGroupVO);

                            return userGroupVO;
                        })
                        .sorted(Comparator.comparing(UserGroupVO::getGmtModify))
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(userGroupVOList, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<Void> bindUserGroup(Long id, String userGroupIds) {
        Preconditions.checkNotNull(id, "员工ID不能为空");
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
        Preconditions.checkNotNull(id, "员工ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupIds), "用户组ID不能为空");

        Arrays.asList(userGroupIds.split(",")).stream()
                .distinct()
                .forEach(userGroupId -> {
                    EmployeeRelaUserGroupDOKey employeeRelaUserGroupDOKey = new EmployeeRelaUserGroupDOKey();
                    employeeRelaUserGroupDOKey.setEmployeeId(id);
                    employeeRelaUserGroupDOKey.setUserGroupId(Long.valueOf(userGroupId));
                    int count = employeeRelaUserGroupDOMapper.deleteByPrimaryKey(employeeRelaUserGroupDOKey);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }

    /**
     * parentId - DOS映射
     *
     * @param employeeDOS
     * @return
     */
    private Map<Long, List<EmployeeDO>> getParentIdDOSMapping(List<EmployeeDO> employeeDOS) {
        if (CollectionUtils.isEmpty(employeeDOS)) {
            return null;
        }

        Map<Long, List<EmployeeDO>> parentIdDOMap = Maps.newConcurrentMap();
        employeeDOS.parallelStream()
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
    private List<LevelVO> parseLevelByLevel(Map<Long, List<EmployeeDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<EmployeeDO> parentDOS = parentIdDOMap.get(-1L);
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
    private void fillChilds(LevelVO parent, Map<Long, List<EmployeeDO>> parentIdDOMap) {
        List<EmployeeDO> childs = parentIdDOMap.get(parent.getValue());
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
     * @param employeeParam
     * @return
     */
    private Long insertAndGetId(EmployeeParam employeeParam) {
        EmployeeDO employeeDO = new EmployeeDO();
        BeanUtils.copyProperties(employeeParam, employeeDO);

        // level
        Long parentId = employeeParam.getParentId();
        if (null != parentId) {
            EmployeeDO parentEmployeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            Preconditions.checkNotNull(parentEmployeeDO, "直接主管不存在");
            Integer parentLevel = parentEmployeeDO.getLevel();
            Integer level = parentLevel == null ? null : parentLevel + 1;
            employeeDO.setLevel(level);
        } else {
            employeeDO.setLevel(1);
        }

        // date
        employeeDO.setGmtCreate(new Date());
        employeeDO.setGmtModify(new Date());

        int count = employeeDOMapper.insertSelective(employeeDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return employeeDO.getId();
    }

    /**
     * 填充员工部门信息
     *
     * @param departmentId
     * @param employeeVO
     */
    private void fillDepartment(Long departmentId, EmployeeVO employeeVO) {
        if (null == departmentId) {
            return;
        }
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO parentDepartment = new BaseVO();
            BeanUtils.copyProperties(departmentDO, parentDepartment);
            // 递归填充所有上层父级部门
            fillSuperDepartment(departmentDO.getParentId(), Lists.newArrayList(parentDepartment), employeeVO);
        }
    }

    private void fillSuperDepartment(Long parentId, List<BaseVO> superDepartmentList, EmployeeVO employeeVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                BaseVO parentDepartment = new BaseVO();
                BeanUtils.copyProperties(departmentDO, parentDepartment);
                superDepartmentList.add(parentDepartment);
                fillSuperDepartment(departmentDO.getParentId(), superDepartmentList, employeeVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superDepartmentList);
            employeeVO.setDepartment(superDepartmentList);
        }
    }

    /**
     * 补充用户组区域信息
     *
     * @param userGroupVO
     */
    private void fillArea(UserGroupVO userGroupVO) {
        List<Long> areaIds = userGroupRelaAreaAuthDOMapper.getAreaIdListByUserGroupId(userGroupVO.getId());
        if (CollectionUtils.isEmpty(areaIds)) {
            return;
        }
        Long areaId = areaIds.get(0);
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {
            BaseVO parentArea = new BaseVO();
            parentArea.setId(baseAreaDO.getAreaId());
            parentArea.setName(baseAreaDO.getAreaName());
            // 递归填充所有上层父级部门
            fillSuperArea(baseAreaDO.getParentAreaId(), Lists.newArrayList(parentArea), userGroupVO);
        }
    }

    /**
     * @param parentId
     * @param superAreaList
     * @param userGroupVO
     */
    private void fillSuperArea(Long parentId, List<BaseVO> superAreaList, UserGroupVO userGroupVO) {
        if (null != parentId) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != baseAreaDO) {
                BaseVO parentArea = new BaseVO();
                parentArea.setId(baseAreaDO.getAreaId());
                parentArea.setName(baseAreaDO.getAreaName());
                superAreaList.add(parentArea);
                fillSuperArea(baseAreaDO.getParentAreaId(), superAreaList, userGroupVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superAreaList);
            userGroupVO.setArea(superAreaList);
        }
    }

    /**
     * 填充用户组部门信息
     *
     * @param departmentId
     * @param userGroupVO
     */
    private void fillDepartment(Long departmentId, UserGroupVO userGroupVO) {
        if (null == departmentId) {
            return;
        }
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO parentDepartment = new BaseVO();
            BeanUtils.copyProperties(departmentDO, parentDepartment);
            // 递归填充所有上层父级部门
            fillSuperDepartment(departmentDO.getParentId(), Lists.newArrayList(parentDepartment), userGroupVO);
        }
    }

    /**
     * 递归填充所有上层父级部门ID
     * <p>
     * 前端需求：仅需要层级ID即可
     *
     * @param parentId
     * @param superDepartmentList
     * @param userGroupVO
     */
    private void fillSuperDepartment(Long parentId, List<BaseVO> superDepartmentList, UserGroupVO userGroupVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                BaseVO parentDepartment = new BaseVO();
                BeanUtils.copyProperties(departmentDO, parentDepartment);
                superDepartmentList.add(parentDepartment);
                fillSuperDepartment(departmentDO.getParentId(), superDepartmentList, userGroupVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superDepartmentList);
            userGroupVO.setDepartment(superDepartmentList);
        }
    }

    /**
     * 填充员工直接主管信息
     *
     * @param parentId
     * @param employeeVO
     */
    private void fillParent(Long parentId, EmployeeVO employeeVO) {
        if (null == parentId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
        if (null != employeeDO) {
            BaseVO parentEmployee = new BaseVO();
            BeanUtils.copyProperties(employeeDO, parentEmployee);
            // 递归填充所有上层父级leader
            fillSupperEmployee(employeeDO.getParentId(), Lists.newArrayList(parentEmployee), employeeVO);
        }
    }

    private void fillSupperEmployee(Long parentId, List<BaseVO> supperEmployeeList, EmployeeVO employeeVO) {
        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                BaseVO parentEmployee = new BaseVO();
                BeanUtils.copyProperties(employeeDO, parentEmployee);
                supperEmployeeList.add(parentEmployee);
                fillSupperEmployee(employeeDO.getParentId(), supperEmployeeList, employeeVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(supperEmployeeList);
            employeeVO.setParent(supperEmployeeList);
        }
    }


    /**
     * 绑定用户组(角色)列表
     *
     * @param employeeId
     * @param userGroupIdList
     */
    private void bindUserGroup(Long employeeId, List<Long> userGroupIdList) {
        if (CollectionUtils.isEmpty(userGroupIdList)) {
            return;
        }

        // 去重
        List<Long> existUserGroupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(employeeId);
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
            List<EmployeeRelaUserGroupDO> employeeRelaUserGroupDOS = userGroupIdList.parallelStream()
                    .map(userGroupId -> {
                        EmployeeRelaUserGroupDO employeeRelaUserGroupDO = new EmployeeRelaUserGroupDO();
                        employeeRelaUserGroupDO.setEmployeeId(employeeId);
                        employeeRelaUserGroupDO.setUserGroupId(userGroupId);
                        employeeRelaUserGroupDO.setGmtCreate(new Date());
                        employeeRelaUserGroupDO.setGmtModify(new Date());

                        return employeeRelaUserGroupDO;
                    })
                    .collect(Collectors.toList());

            int count = employeeRelaUserGroupDOMapper.batchInsert(employeeRelaUserGroupDOS);
            Preconditions.checkArgument(count == employeeRelaUserGroupDOS.size(), "关联失败");
        }
    }
}
