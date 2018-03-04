package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.*;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.query.UserGroupQuery;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.UserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Service
@Transactional
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;
    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private DepartmentDOMapper departmentDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private AuthDOMapper authDOMapper;
    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;
    @Autowired
    private UserGroupRelaAreaAuthDOMapper userGroupRelaAreaAuthDOMapper;


    @Override
    public ResultBean<Long> create(UserGroupParam userGroupParam) {
        Preconditions.checkArgument(null != userGroupParam && StringUtils.isNotBlank(userGroupParam.getName()), "用户组名称不能为空");
        Preconditions.checkNotNull(userGroupParam.getDepartmentId(), "对应部门不能为空");

        // 创建实体，并返回ID
        Long id = insertAndGetId(userGroupParam);

        // 绑定权限列表
        doBindAuth(id, userGroupParam.getAreaId(), userGroupParam.getAuthIdList(), userGroupParam.getType());

        // 绑定员工列表
        doBindEmployee(id, userGroupParam.getEmployeeIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> update(UserGroupParam userGroupParam) {
        Preconditions.checkNotNull(userGroupParam.getId(), "id不能为空");

        UserGroupDO userGroupDO = new UserGroupDO();
        BeanUtils.copyProperties(userGroupParam, userGroupDO);
        userGroupDO.setGmtModify(new Date());
        int count = userGroupDOMapper.updateByPrimaryKeySelective(userGroupDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = userGroupDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<UserGroupVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        UserGroupDO userGroupDO = userGroupDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(userGroupDO, "id有误，数据不存在");

        UserGroupVO userGroupVO = new UserGroupVO();
        BeanUtils.copyProperties(userGroupDO, userGroupVO);

        fillDepartment(userGroupDO.getDepartmentId(), userGroupVO);
        fillArea(userGroupVO);

        return ResultBean.ofSuccess(userGroupVO);
    }

    @Override
    public ResultBean<List<UserGroupVO>> batchGetById(List<Long> idList) {
        Preconditions.checkNotNull(idList, "id不能为空");

        List<UserGroupDO> userGroupDOList = userGroupDOMapper.batchSelectByPrimaryKey(idList, VALID_STATUS);
        Preconditions.checkNotNull(userGroupDOList, "id有误，数据不存在");

        List<UserGroupVO> userGroupVOList = Lists.newArrayList();
        for (UserGroupDO userGroupDO : userGroupDOList) {
            UserGroupVO userGroupVO = new UserGroupVO();
            BeanUtils.copyProperties(userGroupDO, userGroupVO);

            fillDepartment(userGroupDO.getDepartmentId(), userGroupVO);
            fillArea(userGroupVO);
            userGroupVOList.add(userGroupVO);
        }

        return ResultBean.ofSuccess(userGroupVOList);
    }

    @Override
    public ResultBean<List<UserGroupVO>> query(UserGroupQuery query) {
        // 根据departmentId填充所有子部门ID(含自身)
        getAndSetAllChildDepartmentIdList(query);

        int totalNum = userGroupDOMapper.count(query);
        if (totalNum > 0) {

            List<UserGroupDO> userGroupDOS = userGroupDOMapper.query(query);
            if (!CollectionUtils.isEmpty(userGroupDOS)) {

                List<UserGroupVO> departmentVOS = userGroupDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            UserGroupVO userGroupVO = new UserGroupVO();
                            BeanUtils.copyProperties(e, userGroupVO);

                            fillDepartment(e.getDepartmentId(), userGroupVO);
                            fillArea(userGroupVO);

                            return userGroupVO;
                        })
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(departmentVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

//    private void getAndSetDepartmentIdList(UserGroupQuery query) {
//        // getAllDepartmentId
//        List<Long> allDepartmentId = getAllChildDepartmentId(query.getDepartmentId());
//        allDepartmentId.removeAll(Collections.singleton(null));
//        // set
//        query.setDepartmentIdList(allDepartmentId);
//    }

    /**
     * TODO 根据departmentId填充所有子部门ID(含自身)
     *
     * @param query
     * @return
     */
    private List<Long> getAndSetAllChildDepartmentIdList(UserGroupQuery query) {
        // getAll
//        List<MenuDO> menuDOS = departmentDOMapper.getAll(VALID_STATUS);
//
//        // parentId - DOS
//        Map<Long, List<MenuDO>> parentIdDOMap = getParentIdDOSMapping(menuDOS);
//
//        // 递归填充子菜单ID
//        List<Long> childMenuIdList = Lists.newArrayList(parentMenuId);
//        fillAllChildMenuId(parentMenuId, childMenuIdList, parentIdDOMap);
//
//        return childMenuIdList;

        return null;
    }

    @Override
    public ResultBean<List<AuthVO>> listAuth(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "用户组ID不能为空");


        return ResultBean.ofSuccess(Collections.EMPTY_LIST);
    }

    @Override
    public ResultBean<List<EmployeeVO>> listBindEmployee(EmployeeQuery query) {
        Preconditions.checkNotNull(query.getUserGroupId(), "用户组ID不能为空");

        // queryAll
        List<Long> allEmployeeIdListByCondition = employeeDOMapper.queryAllEmployeeIdList(query);

        // 已绑定IDList
        List<Long> hasBindEmployeeIdList = employeeRelaUserGroupDOMapper.getEmployeeIdListByUserGroupId(query.getUserGroupId());

        // 符合条件的 - 已绑定IDList
        List<Long> hasBindEmployeeIdListByCondition = allEmployeeIdListByCondition.parallelStream()
                .filter(Objects::nonNull)
                .distinct()
                .map(e -> {
                    if (hasBindEmployeeIdList.contains(e)) {
                        return e;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        // 分页
        if (!CollectionUtils.isEmpty(hasBindEmployeeIdListByCondition)) {

            // 分页ID截取
            List<Long> pageEmployeeIdList = getPageEmployeeIdList(query.getStartRow(), query.getPageSize(), hasBindEmployeeIdListByCondition);

            if (!CollectionUtils.isEmpty(pageEmployeeIdList)) {
                // 获取实体
                List<EmployeeDO> employeeDOS = employeeDOMapper.getByIdList(pageEmployeeIdList);

                // convert
                if (!CollectionUtils.isEmpty(employeeDOS)) {
                    List<EmployeeVO> employeeVOS = employeeDOS.stream()
                            .filter(Objects::nonNull)
                            .map(e -> {
                                EmployeeVO employeeVO = new EmployeeVO();
                                BeanUtils.copyProperties(e, employeeVO);

                                fillDepartment(e.getDepartmentId(), employeeVO);
                                fillLeader(e.getParentId(), employeeVO);

                                return employeeVO;
                            })
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparing(EmployeeVO::getId))
                            .collect(Collectors.toList());
                    return ResultBean.ofSuccess(employeeVOS, hasBindEmployeeIdListByCondition.size(), query.getPageIndex(), query.getPageSize());
                }
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, hasBindEmployeeIdListByCondition.size(), query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<EmployeeVO>> listUnbindEmployee(EmployeeQuery query) {
        Preconditions.checkNotNull(query.getUserGroupId(), "用户组ID不能为空");

        // queryAll
        List<Long> allEmployeeIdListByCondition = employeeDOMapper.queryAllEmployeeIdList(query);

        // 已绑定IDList
        List<Long> hasBindEmployeeIdList = employeeRelaUserGroupDOMapper.getEmployeeIdListByUserGroupId(query.getUserGroupId());

        // 未绑定List
        allEmployeeIdListByCondition.removeAll(hasBindEmployeeIdList);
        List<Long> unBindEmployeeIdListByCondition = allEmployeeIdListByCondition.parallelStream()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        // 分页
        if (!CollectionUtils.isEmpty(unBindEmployeeIdListByCondition)) {

            // 分页ID截取
            List<Long> pageEmployeeIdList = getPageEmployeeIdList(query.getStartRow(), query.getPageSize(), unBindEmployeeIdListByCondition);

            if (!CollectionUtils.isEmpty(pageEmployeeIdList)) {
                // 获取实体
                List<EmployeeDO> employeeDOS = employeeDOMapper.getByIdList(pageEmployeeIdList);

                // convert
                if (!CollectionUtils.isEmpty(employeeDOS)) {
                    List<EmployeeVO> employeeVOS = employeeDOS.stream()
                            .filter(Objects::nonNull)
                            .map(e -> {
                                EmployeeVO employeeVO = new EmployeeVO();
                                BeanUtils.copyProperties(e, employeeVO);

                                fillDepartment(e.getDepartmentId(), employeeVO);
                                fillLeader(e.getParentId(), employeeVO);

                                return employeeVO;
                            })
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparing(EmployeeVO::getId))
                            .collect(Collectors.toList());
                    return ResultBean.ofSuccess(employeeVOS, unBindEmployeeIdListByCondition.size(), query.getPageIndex(), query.getPageSize());
                }
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, unBindEmployeeIdListByCondition.size(), query.getPageIndex(), query.getPageSize());
    }

    /**
     * 分页ID截取
     *
     * @param startRow
     * @param pageSize
     * @param employeeIdList
     * @return
     */
    private List<Long> getPageEmployeeIdList(Integer startRow, Integer pageSize, List<Long> employeeIdList) {
        int fromIndex = 0;
        int toIndex = 0;
        int totalNum = employeeIdList.size();

        if (startRow > totalNum) {
            return null;
        } else {
            fromIndex = startRow;
        }

        if (pageSize + startRow > totalNum) {
            toIndex = totalNum;
        } else {
            toIndex = pageSize + startRow;
        }
        List<Long> pageEmployeeIdList = employeeIdList.subList(fromIndex, toIndex);
        return pageEmployeeIdList;
    }

    /**
     * 检验并设置是否已选中
     *
     * @param employeeVO
     * @param allHasBindEmployeeIdList
     */

    private void checkAndSetHasSelected(EmployeeVO employeeVO, List<Long> allHasBindEmployeeIdList) {
        if (!CollectionUtils.isEmpty(allHasBindEmployeeIdList)) {
            if (allHasBindEmployeeIdList.contains(employeeVO.getId())) {
                employeeVO.setSelected(true);
            } else {
                employeeVO.setSelected(false);
            }
        } else {
            employeeVO.setSelected(false);
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
            // 递归填充所有上层父级区域
            fillSupperArea(baseAreaDO.getParentAreaId(), Lists.newArrayList(parentArea), userGroupVO);
        }
    }

    /**
     * 递归填充所有上层父级区域
     *
     * @param parentId
     * @param supperAreaList
     * @param userGroupVO
     */
    private void fillSupperArea(Long parentId, List<BaseVO> supperAreaList, UserGroupVO userGroupVO) {
        if (null != parentId) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != baseAreaDO) {
                BaseVO parentArea = new BaseVO();
                parentArea.setId(baseAreaDO.getAreaId());
                parentArea.setName(baseAreaDO.getAreaName());
                supperAreaList.add(parentArea);
                fillSupperArea(baseAreaDO.getParentAreaId(), supperAreaList, userGroupVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(supperAreaList);
            userGroupVO.setArea(supperAreaList);
        }
    }

    /**
     * 填充员工部门信息
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
     * 递归填充所有上层父级部门
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
     * 填充员工直接主管信息
     *
     * @param parentId
     * @param employeeVO
     */
    private void fillLeader(Long parentId, EmployeeVO employeeVO) {
        if (null == parentId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
        if (null != employeeDO) {
            BaseVO parentEmployee = new BaseVO();
            BeanUtils.copyProperties(employeeDO, parentEmployee);
            // 递归填充所有上层父级部门
            fillSuperLeader(employeeDO.getParentId(), Lists.newArrayList(parentEmployee), employeeVO);
        }
    }

    /**
     * 递归填充所有上层父级主管ID
     *
     * @param parentId
     * @param superLeaderList
     * @param employeeVO
     */
    private void fillSuperLeader(Long parentId, List<BaseVO> superLeaderList, EmployeeVO employeeVO) {
        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                BaseVO parentEmployee = new BaseVO();
                BeanUtils.copyProperties(employeeDO, parentEmployee);
                superLeaderList.add(parentEmployee);
                fillSuperLeader(employeeDO.getParentId(), superLeaderList, employeeVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superLeaderList);
            employeeVO.setParent(superLeaderList);
        }
    }

    @Override
    public ResultBean<Void> bindEmployee(Long id, String employeeIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeIds), "员工ID列表不能为空");

        // convert
        List<Long> employeeIdList = Arrays.asList(employeeIds.split(",")).parallelStream()
                .filter(Objects::nonNull)
                .distinct()
                .map(e -> {
                    Long employeeId = Long.valueOf(e);
                    return employeeId;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 执行绑定
        doBindEmployee(id, employeeIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    public ResultBean<Void> unbindEmployee(Long id, String employeeIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeIds), "员工ID列表不能为空");

        Arrays.asList(employeeIds.split(",")).stream()
                .distinct()
                .forEach(employeeId -> {
                    EmployeeRelaUserGroupDO employeeRelaUserGroupDO = new EmployeeRelaUserGroupDO();
                    employeeRelaUserGroupDO.setUserGroupId(id);
                    employeeRelaUserGroupDO.setEmployeeId(Long.valueOf(employeeId));
                    int count = employeeRelaUserGroupDOMapper.deleteByPrimaryKey(employeeRelaUserGroupDO);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }

    @Override
    public ResultBean<Void> editAuth(Long id, Long areaId, String authIds, Byte type) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkNotNull(areaId, "限定权限使用的业务区域范围不能为空");
        Preconditions.checkNotNull(type, "权限类型不能为空");

        // 删除所有旧有的
        delAllOldAuth(id, type);

        if (StringUtils.isNotBlank(authIds)) {
            // convert
            List<Long> authIdList = Arrays.asList(authIds.split(",")).stream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        return Long.valueOf(e);
                    })
                    .distinct()
                    .collect(Collectors.toList());

            // 执行绑定
            doBindAuth(id, areaId, authIdList, type);
        }

        return ResultBean.ofSuccess(null, "编辑权限成功");
    }

    /**
     * 删除所有
     *
     * @param userGroupId
     * @param type
     */
    private void delAllOldAuth(Long userGroupId, Byte type) {
        // 删除所有
        userGroupRelaAreaAuthDOMapper.deleteAllByUserGroupIdAndType(userGroupId, type);
    }

    @Override
    public ResultBean<Void> bindAuth(Long id, Long areaId, String authIds, Byte type) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkNotNull(areaId, "限定权限使用的业务区域范围不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(authIds), "权限ID列表不能为空");
        Preconditions.checkNotNull(type, "权限类型不能为空");

        // convert
        List<Long> authIdList = Arrays.asList(authIds.split(",")).stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    return Long.valueOf(e);
                })
                .distinct()
                .collect(Collectors.toList());

        // insert new
        doBindAuth(id, areaId, authIdList, type);

        // update old areaId
        updateRelaAuthArea(id, areaId);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    public ResultBean<Void> unbindAuth(Long id, String authIds, Byte type) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(authIds), "权限ID列表不能为空");
        Preconditions.checkNotNull(type, "权限类型不能为空");

        // delete
        Arrays.asList(authIds.split(",")).stream()
                .distinct()
                .forEach(authId -> {
                    UserGroupRelaAreaAuthDOKey userGroupRelaAreaAuthDOKey = new UserGroupRelaAreaAuthDOKey();
                    userGroupRelaAreaAuthDOKey.setUserGroupId(id);
                    userGroupRelaAreaAuthDOKey.setAuthId(Long.valueOf(authId));
                    int count = userGroupRelaAreaAuthDOMapper.deleteByPrimaryKey(userGroupRelaAreaAuthDOKey);
                    Preconditions.checkArgument(count > 0, "取消关联失败,权限不存在或权限已取消!");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }

    /**
     * 插入实体，并返回主键ID
     *
     * @param userGroupParam
     * @return
     */
    private Long insertAndGetId(UserGroupParam userGroupParam) {
        List<String> nameList = userGroupDOMapper.getAllName(VALID_STATUS);
        Preconditions.checkArgument(!nameList.contains(userGroupParam.getName()), "用户组名称已存在");

        UserGroupDO userGroupDO = new UserGroupDO();
        BeanUtils.copyProperties(userGroupParam, userGroupDO);
        userGroupDO.setStatus(VALID_STATUS);
        userGroupDO.setGmtCreate(new Date());
        userGroupDO.setGmtModify(new Date());

        int count = userGroupDOMapper.insertSelective(userGroupDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return userGroupDO.getId();
    }

    /**
     * 绑定权限列表
     *
     * @param userGroupId
     * @param areaId
     * @param authIdList
     * @param type        权限类型  1:MENU; 2:PAGE; 3:OPERATION;
     */
    private void doBindAuth(Long userGroupId, Long areaId, List<Long> authIdList, Byte type) {
        if (CollectionUtils.isEmpty(authIdList)) {
            return;
        }
        Preconditions.checkNotNull(areaId, "限定权限使用的业务区域范围不能为空");

        // auth实体ID 去重
        distinctAuthIdList(userGroupId, authIdList, type);

        // convert  根据权限类型 将权限实体ID 映射到 AuthID
        List<Long> trueAuthIdList = convertAuthEntityIdToAuthId(authIdList, type);

        // 执行绑定
        execBindAuth(userGroupId, areaId, trueAuthIdList);
    }

    /**
     * 根据权限类型 将权限实体ID 映射到 AuthID
     *
     * @param authEntityIdList
     * @param type             权限类型  1:MENU; 2:PAGE; 3:OPERATION;
     * @return
     */
    private List<Long> convertAuthEntityIdToAuthId(List<Long> authEntityIdList, Byte type) {
        if (!CollectionUtils.isEmpty(authEntityIdList)) {
            List<Long> trueAuthIdList = authDOMapper.convertToAuthId(authEntityIdList, type);
            return trueAuthIdList;
        }
        return null;
    }

    /**
     * 替换所有的areaId
     * <p>
     * 需求：所有的权限都只限制在当前的城市下！！！全部权限绑定同一个城市，故以当前areaId为准！！！
     *
     * @param userGroupId
     * @param areaId
     */
    private void updateRelaAuthArea(Long userGroupId, Long areaId) {
        UserGroupRelaAreaAuthDO userGroupRelaAreaAuthDO = new UserGroupRelaAreaAuthDO();
        userGroupRelaAreaAuthDO.setUserGroupId(userGroupId);
        userGroupRelaAreaAuthDO.setAreaId(areaId);
        userGroupRelaAreaAuthDO.setGmtModify(new Date());
        int count = userGroupRelaAreaAuthDOMapper.updateByUserGroupIdSelective(userGroupRelaAreaAuthDO);
        Preconditions.checkArgument(count > 0, "关联失败");
    }

    /**
     * 权限实体ID(source_id)去重
     *
     * @param userGroupId
     * @param authEntityIdList
     * @param type
     */
    private void distinctAuthIdList(Long userGroupId, List<Long> authEntityIdList, Byte type) {
        // 已绑定权限实体ID列表
        List<Long> hasBindAuthEntityIdList = userGroupRelaAreaAuthDOMapper.getHasBindAuthEntityIdListByUserGroupIdAndType(userGroupId, type);

        // 去重
        if (!CollectionUtils.isEmpty(hasBindAuthEntityIdList)) {

            List<Long> repeatTmp = Lists.newArrayList();
            authEntityIdList.parallelStream()
                    .forEach(e -> {
                        if (hasBindAuthEntityIdList.contains(e)) {
                            repeatTmp.add(e);
                        }
                    });

            authEntityIdList.removeAll(repeatTmp);
        }
    }

    /**
     * 执行绑定
     *
     * @param userGroupId
     * @param areaId
     * @param authIdList
     */
    private void execBindAuth(Long userGroupId, Long areaId, List<Long> authIdList) {
        if (CollectionUtils.isEmpty(authIdList)) {
            return;
        }
        List<UserGroupRelaAreaAuthDO> userGroupRelaAreaAuthDOS = authIdList.parallelStream()
                .filter(Objects::nonNull)
                .distinct()
                .map(authId -> {

                    UserGroupRelaAreaAuthDO userGroupRelaAreaAuthDO = new UserGroupRelaAreaAuthDO();
                    userGroupRelaAreaAuthDO.setUserGroupId(userGroupId);
                    userGroupRelaAreaAuthDO.setAreaId(areaId);
                    userGroupRelaAreaAuthDO.setAuthId(authId);
                    userGroupRelaAreaAuthDO.setGmtCreate(new Date());
                    userGroupRelaAreaAuthDO.setGmtModify(new Date());

                    return userGroupRelaAreaAuthDO;
                })
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(userGroupRelaAreaAuthDOS)) {
            int count = userGroupRelaAreaAuthDOMapper.batchInsert(userGroupRelaAreaAuthDOS);
            Preconditions.checkArgument(count == userGroupRelaAreaAuthDOS.size(), "关联权限失败");
        }
    }

    /**
     * 绑定员工列表
     *
     * @param userGroupId
     * @param employeeIdList
     */
    private void doBindEmployee(Long userGroupId, List<Long> employeeIdList) {
        if (CollectionUtils.isEmpty(employeeIdList)) {
            return;
        }

        // 去重
        List<Long> existEmployeeIdList = employeeRelaUserGroupDOMapper.getEmployeeIdListByUserGroupId(userGroupId);
        if (!CollectionUtils.isEmpty(existEmployeeIdList)) {

            employeeIdList = employeeIdList.parallelStream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(e -> {
                        if (!existEmployeeIdList.contains(e)) {
                            return e;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }

        // 绑定
        if (!CollectionUtils.isEmpty(employeeIdList)) {
            List<EmployeeRelaUserGroupDO> employeeRelaUserGroupDOS = employeeIdList.parallelStream()
                    .map(e -> {
                        EmployeeRelaUserGroupDO employeeRelaUserGroupDO = new EmployeeRelaUserGroupDO();
                        employeeRelaUserGroupDO.setUserGroupId(userGroupId);
                        employeeRelaUserGroupDO.setEmployeeId(e);
                        employeeRelaUserGroupDO.setGmtCreate(new Date());
                        employeeRelaUserGroupDO.setGmtModify(new Date());

                        return employeeRelaUserGroupDO;
                    })
                    .collect(Collectors.toList());

            int count = employeeRelaUserGroupDOMapper.batchInsert(employeeRelaUserGroupDOS);
            Preconditions.checkArgument(count == employeeRelaUserGroupDOS.size(), "关联员工失败");
        }
    }
}
