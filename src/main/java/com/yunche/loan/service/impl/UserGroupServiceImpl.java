package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.UserGroupQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.viewObj.*;
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
    private DepartmentRelaUserGroupDOMapper departmentRelaUserGroupDOMapper;
    @Autowired
    private UserGroupRelaAreaDOMapper userGroupRelaAreaDOMapper;
    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;
    @Autowired
    private UserGroupRelaAreaAuthDOMapper userGroupRelaAreaAuthDOMapper;

    @Override
    public ResultBean<Long> create(UserGroupParam userGroupParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupParam.getName()), "用户组名称不能为空");
        Preconditions.checkNotNull(userGroupParam.getDepartmentId(), "对应部门不能为空");

        // 创建实体，并返回ID
        Long id = insertAndGetId(userGroupParam);

        // 绑定部门
//        bindDepartment(id, userGroupParam.getDepartmentId());

        // 绑定权限列表
        bindAuth(id, userGroupParam.getAreaId(), userGroupParam.getAuthIdList());

        // 绑定员工列表
        bindEmployee(id, userGroupParam.getEmployeeIdList());

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

        // 编辑部门
//        updateDepartment(userGroupParam.getId(), userGroupParam.getDepartmentId());

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

        UserGroupDO userGroupDO = userGroupDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(userGroupDO, "id有误，数据不存在");

        UserGroupVO userGroupVO = new UserGroupVO();
        BeanUtils.copyProperties(userGroupDO, userGroupVO);

        fillDepartment(userGroupDO.getDepartmentId(), userGroupVO);
        fillArea(userGroupVO);

        return ResultBean.ofSuccess(userGroupVO);
    }

    @Override
    public ResultBean<List<UserGroupVO>> query(UserGroupQuery query) {
        int totalNum = userGroupDOMapper.count(query);
        if (totalNum < 1) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<UserGroupDO> userGroupDOS = userGroupDOMapper.query(query);
        if (CollectionUtils.isEmpty(userGroupDOS)) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

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

    @Override
    public ResultBean<List<AuthVO>> listAuth(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "用户组ID不能为空");


        return ResultBean.ofSuccess(Collections.EMPTY_LIST);
    }

    @Override
    public ResultBean<List<EmployeeVO>> listEmployee(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "用户组ID不能为空");

        int totalNum = employeeDOMapper.countListEmployeeByUserGroupId(query);
        if (totalNum > 0) {
            List<EmployeeDO> employeeDOS = employeeDOMapper.listEmployeeByUserGroupId(query);
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
                        .collect(Collectors.toList());
                return ResultBean.ofSuccess(employeeVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    /**
     * 绑定部门
     *
     * @param userGroupId
     * @param departmentId
     */
//    private void bindDepartment(Long userGroupId, Long departmentId) {
//        DepartmentRelaUserGroupDO departmentRelaUserGroupDO = new DepartmentRelaUserGroupDO();
//        departmentRelaUserGroupDO.setUserGroupId(userGroupId);
//        departmentRelaUserGroupDO.setDepartmentId(departmentId);
//        departmentRelaUserGroupDO.setGmtCreate(new Date());
//        departmentRelaUserGroupDO.setGmtModify(new Date());
//        int count = departmentRelaUserGroupDOMapper.insert(departmentRelaUserGroupDO);
//        Preconditions.checkArgument(count > 0, "关联部门失败");
//    }

    /**
     * 绑定区域(城市)
     *
     * @param userGroupId
     * @param areaId
     */
    private void bindArea(Long userGroupId, Long areaId) {
        UserGroupRelaAreaDO userGroupRelaAreaDO = new UserGroupRelaAreaDO();
        userGroupRelaAreaDO.setUserGroupId(userGroupId);
        userGroupRelaAreaDO.setAreaId(areaId);
        userGroupRelaAreaDO.setGmtCreate(new Date());
        userGroupRelaAreaDO.setGmtModify(new Date());
        int count = userGroupRelaAreaDOMapper.insert(userGroupRelaAreaDO);
        Preconditions.checkArgument(count > 0, "关联区域失败");
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

            // 递归填充所有上层父级部门
            fillAreaIds(baseAreaDO.getParentAreaId(), Lists.newArrayList(baseAreaDO.getAreaId()), userGroupVO);
        }
    }

    /**
     * @param parentId
     * @param area
     * @param userGroupVO
     */
    private void fillAreaIds(Long parentId, List<Long> area, UserGroupVO userGroupVO) {
        if (null != parentId) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != baseAreaDO) {
                area.add(parentId);
                fillParentDepartmentIds(baseAreaDO.getParentAreaId(), area, userGroupVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(area);
            userGroupVO.setArea(area);
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
            // 递归填充所有上层父级部门
            fillParentDepartmentIds(departmentDO.getParentId(), Lists.newArrayList(departmentDO.getId()), userGroupVO);
        }
    }

    /**
     * 递归填充所有上层父级部门ID
     * <p>
     * 前端需求：仅需要层级ID即可
     *
     * @param parentId
     * @param department
     * @param userGroupVO
     */
    private void fillParentDepartmentIds(Long parentId, List<Long> department, UserGroupVO userGroupVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                department.add(parentId);
                fillParentDepartmentIds(departmentDO.getParentId(), department, userGroupVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(department);
            userGroupVO.setDepartment(department);
        }
    }

    private void fillDepartment(Long departmentId, EmployeeVO employeeVO) {
        if (null == departmentId) {
            return;
        }
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            // 递归填充所有上层父级部门
            fillParentDepartmentIds(departmentDO.getParentId(), Lists.newArrayList(departmentDO.getId()), employeeVO);
        }
    }

    private void fillParentDepartmentIds(Long parentId, List<Long> department, EmployeeVO employeeVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                department.add(parentId);
                fillParentDepartmentIds(departmentDO.getParentId(), department, employeeVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(department);
            employeeVO.setDepartment(department);
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
            // 递归填充所有上层父级部门
            fillParentLeaderIds(employeeDO.getParentId(), Lists.newArrayList(employeeDO.getId()), employeeVO);
        }
    }

    /**
     * 递归填充所有上层父级主管ID
     *
     * @param parentId
     * @param leader
     * @param employeeVO
     */
    private void fillParentLeaderIds(Long parentId, List<Long> leader, EmployeeVO employeeVO) {
        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                leader.add(parentId);
                fillParentDepartmentIds(employeeDO.getParentId(), leader, employeeVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(leader);
            employeeVO.setDepartment(leader);
        }
    }

    @Override
    public ResultBean<Void> bindEmployee(Long id, String employeeIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeIds), "员工ID列表不能为空");

        // 去重
        List<Long> employeeIdList = distinctEmployeeIds(id, employeeIds);

        // 绑定
        if (!CollectionUtils.isEmpty(employeeIdList)) {
            List<EmployeeRelaUserGroupDO> employeeRelaUserGroupDOS = employeeIdList.parallelStream()
                    .map(employeeId -> {
                        EmployeeRelaUserGroupDO employeeRelaUserGroupDO = new EmployeeRelaUserGroupDO();
                        employeeRelaUserGroupDO.setUserGroupId(id);
                        employeeRelaUserGroupDO.setEmployeeId(employeeId);
                        employeeRelaUserGroupDO.setGmtCreate(new Date());
                        employeeRelaUserGroupDO.setGmtModify(new Date());

                        return employeeRelaUserGroupDO;
                    })
                    .collect(Collectors.toList());

            int count = employeeRelaUserGroupDOMapper.batchInsert(employeeRelaUserGroupDOS);
            Preconditions.checkArgument(count == employeeRelaUserGroupDOS.size(), "关联失败");
        }

        return ResultBean.ofSuccess(null, "关联成功");
    }

    /**
     * 去重
     *
     * @param userGroupId
     * @param employeeIds
     * @return
     */
    private List<Long> distinctEmployeeIds(Long userGroupId, String employeeIds) {
        List<Long> existEmployeeIdList = employeeRelaUserGroupDOMapper.getEmployeeIdListByUserGroupId(userGroupId);
        List<Long> employeeIdList = null;
        if (!CollectionUtils.isEmpty(existEmployeeIdList)) {

            employeeIdList = Arrays.asList(employeeIds.split(",")).parallelStream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(e -> {
                        Long employeeId = Long.valueOf(e);
                        if (!existEmployeeIdList.contains(employeeId)) {
                            return employeeId;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } else {
            employeeIdList = Arrays.asList(employeeIds.split(",")).parallelStream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(e -> {
                        Long employeeId = Long.valueOf(e);
                        return employeeId;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return employeeIdList;
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
    public ResultBean<Void> bindAuth(Long id, Long areaId, String authIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkNotNull(areaId, "限定权限使用的业务区域ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(authIds), "权限ID列表不能为空");

        // convert
        List<Long> authIdList = Arrays.asList(authIds.split(",")).stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    return Long.valueOf(e);
                })
                .distinct()
                .collect(Collectors.toList());

        // insert new
        bindAuth(id, areaId, authIdList);

        // update old areaId
        updateRelaAuthArea(id, areaId, authIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    public ResultBean<Void> unbindAuth(Long id, String authIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(authIds), "权限ID列表不能为空");

        // delete
        Arrays.asList(authIds.split(",")).stream()
                .distinct()
                .forEach(authId -> {
                    UserGroupRelaAreaAuthDOKey userGroupRelaAreaAuthDOKey = new UserGroupRelaAreaAuthDOKey();
                    userGroupRelaAreaAuthDOKey.setUserGroupId(id);
                    userGroupRelaAreaAuthDOKey.setAuthId(Long.valueOf(authId));
                    int count = userGroupRelaAreaAuthDOMapper.deleteByPrimaryKey(userGroupRelaAreaAuthDOKey);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
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
     * 编辑部门
     *
     * @param userGroupId
     * @param departmentId
     */
//    private void updateDepartment(Long userGroupId, Long departmentId) {
//        // get
//        List<DepartmentRelaUserGroupDO> departmentRelaUserGroupDOS = departmentRelaUserGroupDOMapper.getByUserGroupId(userGroupId);
//        if (CollectionUtils.isEmpty(departmentRelaUserGroupDOS)) {
//            // insert
//            DepartmentRelaUserGroupDO departmentRelaUserGroupDO = new DepartmentRelaUserGroupDO();
//            departmentRelaUserGroupDO.setUserGroupId(userGroupId);
//            departmentRelaUserGroupDO.setDepartmentId(departmentId);
//            departmentRelaUserGroupDO.setGmtCreate(new Date());
//            departmentRelaUserGroupDO.setGmtModify(new Date());
//            int count = departmentRelaUserGroupDOMapper.insert(departmentRelaUserGroupDO);
//            Preconditions.checkArgument(count > 0, "编辑部门失败");
//        } else {
//            // update : del + insert
//            // delete old
//            DepartmentRelaUserGroupDO departmentRelaUserGroupDO = departmentRelaUserGroupDOS.get(0);
//            int delCount = departmentRelaUserGroupDOMapper.deleteByPrimaryKey(departmentRelaUserGroupDO);
//            Preconditions.checkArgument(delCount > 0, "编辑部门失败");
//
//            // insert new
//            departmentRelaUserGroupDO.setDepartmentId(departmentId);
//            departmentRelaUserGroupDO.setGmtModify(new Date());
//            int count = departmentRelaUserGroupDOMapper.insert(departmentRelaUserGroupDO);
//            Preconditions.checkArgument(count > 0, "编辑部门失败");
//        }
//    }

    /**
     * 编辑区域(城市)
     *
     * @param userGroupId
     * @param areaId
     */
//    private void updateArea(Long userGroupId, Long areaId) {
//        // get
//        List<UserGroupRelaAreaDO> userGroupRelaAreaDOS = userGroupRelaAreaDOMapper.getByUserGroupId(userGroupId);
//        if (CollectionUtils.isEmpty(userGroupRelaAreaDOS)) {
//            // insert
//            UserGroupRelaAreaDO userGroupRelaAreaDO = new UserGroupRelaAreaDO();
//            userGroupRelaAreaDO.setUserGroupId(userGroupId);
//            userGroupRelaAreaDO.setAreaId(areaId);
//            userGroupRelaAreaDO.setGmtCreate(new Date());
//            userGroupRelaAreaDO.setGmtModify(new Date());
//            int count = userGroupRelaAreaDOMapper.insert(userGroupRelaAreaDO);
//            Preconditions.checkArgument(count > 0, "编辑区域失败");
//        } else {
//            // update : del + insert
//            // delete old
//            UserGroupRelaAreaDO userGroupRelaAreaDO = userGroupRelaAreaDOS.get(0);
//            int delCount = userGroupRelaAreaDOMapper.deleteByPrimaryKey(userGroupRelaAreaDO);
//            Preconditions.checkArgument(delCount > 0, "编辑区域失败");
//
//            // insert new
//            userGroupRelaAreaDO.setAreaId(areaId);
//            userGroupRelaAreaDO.setGmtModify(new Date());
//            int insertCount = userGroupRelaAreaDOMapper.insert(userGroupRelaAreaDO);
//            Preconditions.checkArgument(insertCount > 0, "编辑区域失败");
//        }
//    }

    /**
     * 绑定权限列表
     *
     * @param userGroupId
     * @param areaId
     * @param authIdList
     */
    private void bindAuth(Long userGroupId, Long areaId, List<Long> authIdList) {
        if (CollectionUtils.isEmpty(authIdList)) {
            return;
        }

        // 去重
        distinctAuthIdList(userGroupId, areaId, authIdList);

        // 执行绑定
        execBindAuth(userGroupId, areaId, authIdList);
    }

    /**
     * 替换所有的areaId
     * <p>
     * 需求：所有的权限都只限制在当前的城市下！！！全部权限绑定同一个城市，故以当前areaId为准！！！
     *
     * @param userGroupId
     * @param areaId
     * @param authIdList
     */
    private void updateRelaAuthArea(Long userGroupId, Long areaId, List<Long> authIdList) {
        UserGroupRelaAreaAuthDO userGroupRelaAreaAuthDO = new UserGroupRelaAreaAuthDO();
        userGroupRelaAreaAuthDO.setUserGroupId(userGroupId);
        userGroupRelaAreaAuthDO.setAreaId(areaId);
        userGroupRelaAreaAuthDO.setGmtModify(new Date());
        int count = userGroupRelaAreaAuthDOMapper.updateByUserGroupIdSelective(userGroupRelaAreaAuthDO);
        Preconditions.checkArgument(count > 0, "关联失败");
    }

    /**
     * 权限ID去重
     *
     * @param userGroupId
     * @param areaId
     * @param authIdList
     */
    private void distinctAuthIdList(Long userGroupId, Long areaId, List<Long> authIdList) {
        UserGroupRelaAreaAuthDOKey userGroupRelaAreaAuthDOKey = new UserGroupRelaAreaAuthDOKey();
        userGroupRelaAreaAuthDOKey.setUserGroupId(userGroupId);
        userGroupRelaAreaAuthDOKey.setAreaId(areaId);
        List<UserGroupRelaAreaAuthDO> existUserGroupRelaAreaAuthDOS = userGroupRelaAreaAuthDOMapper.query(userGroupRelaAreaAuthDOKey);
        if (!CollectionUtils.isEmpty(existUserGroupRelaAreaAuthDOS)) {
            List<Long> existAuthIdList = existUserGroupRelaAreaAuthDOS.parallelStream()
                    .filter(e -> null != e && null != e.getAreaId())
                    .map(e -> {
                        return e.getAreaId();
                    })
                    .distinct()
                    .collect(Collectors.toList());

            List<Long> repeatTmp = Lists.newArrayList();
            authIdList.parallelStream()
                    .forEach(e -> {
                        if (existAuthIdList.contains(e)) {
                            repeatTmp.add(e);
                        }
                    });

            authIdList.removeAll(repeatTmp);
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

        int count = userGroupRelaAreaAuthDOMapper.batchInsert(userGroupRelaAreaAuthDOS);
        Preconditions.checkArgument(count == userGroupRelaAreaAuthDOS.size(), "关联权限失败");
    }

    /**
     * 绑定员工列表
     *
     * @param userGroupId
     * @param employeeIdList
     */
    private void bindEmployee(Long userGroupId, List<Long> employeeIdList) {
        if (CollectionUtils.isEmpty(employeeIdList)) {
            return;
        }

        // 去重
        List<Long> existEmployeeIdList = employeeRelaUserGroupDOMapper.getEmployeeIdListByUserGroupId(userGroupId);
        if (!CollectionUtils.isEmpty(existEmployeeIdList)) {

            employeeIdList = existEmployeeIdList.parallelStream()
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
