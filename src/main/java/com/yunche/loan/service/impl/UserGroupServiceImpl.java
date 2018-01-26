package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.UserGroupQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.BaseVO;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
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
    private EmployeeRelaDepartmentDOMapper employeeRelaDepartmentDOMapper;


    @Override
    public ResultBean<Long> create(UserGroupParam userGroupParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupParam.getName()), "用户组名称不能为空");
        Preconditions.checkNotNull(userGroupParam.getDepartmentId(), "对应部门不能为空");

        // 创建实体，并返回ID
        Long id = insertAndGetId(userGroupParam);

        // 绑定部门
        bindDepartment(id, userGroupParam.getDepartmentId());

        // 绑定区域(城市)
        bindArea(id, userGroupParam.getAreaId());

        // 绑定权限列表
        bindAuth(id, userGroupParam.getAuthIdList());

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
        updateDepartment(userGroupParam.getId(), userGroupParam.getDepartmentId());

        // 编辑区域(城市)
        updateArea(userGroupParam.getId(), userGroupParam.getAreaId());

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

        fillDepartment(userGroupVO);
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

                    fillDepartment(userGroupVO);
                    fillArea(userGroupVO);

                    return userGroupVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(departmentVOS, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<AuthVO>> listAuth(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "id不能为空");


        return ResultBean.ofSuccess(Collections.EMPTY_LIST);
    }

    @Override
    public ResultBean<List<EmployeeVO>> listEmployee(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "id不能为空");

        int totalNum = employeeRelaUserGroupDOMapper.count(query);
        if (totalNum > 0) {
            List<EmployeeRelaUserGroupDO> employeeRelaUserGroupDOS = employeeRelaUserGroupDOMapper.query(query);
            List<EmployeeVO> employeeVOS = Collections.EMPTY_LIST;
            if (!CollectionUtils.isEmpty(employeeRelaUserGroupDOS)) {
                employeeVOS = employeeRelaUserGroupDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(e.getUserGroupId(), VALID_STATUS);
                            if (null != employeeDO) {
                                EmployeeVO employeeVO = new EmployeeVO();
                                BeanUtils.copyProperties(employeeDO, employeeVO);

                                fillDepartment(employeeVO);
                                fillLeader(employeeDO.getParentId(), employeeVO);

                                return employeeVO;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            return ResultBean.ofSuccess(employeeVOS, totalNum, query.getPageIndex(), query.getPageSize());
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST);
    }

    /**
     * 绑定部门
     *
     * @param userGroupId
     * @param departmentId
     */
    private void bindDepartment(Long userGroupId, Long departmentId) {
        DepartmentRelaUserGroupDO departmentRelaUserGroupDO = new DepartmentRelaUserGroupDO();
        departmentRelaUserGroupDO.setUserGroupId(userGroupId);
        departmentRelaUserGroupDO.setDepartmentId(departmentId);
        departmentRelaUserGroupDO.setGmtCreate(new Date());
        departmentRelaUserGroupDO.setGmtModify(new Date());
        int count = departmentRelaUserGroupDOMapper.insert(departmentRelaUserGroupDO);
        Preconditions.checkArgument(count > 0, "关联部门失败");
    }

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
     * 补充用户组部门信息
     *
     * @param userGroupVO
     */
    private void fillDepartment(UserGroupVO userGroupVO) {
        List<Long> departmentIds = departmentRelaUserGroupDOMapper.getDepartmentIdListByUserGroupId(userGroupVO.getId());
        if (CollectionUtils.isEmpty(departmentIds)) {
            return;
        }
        Long departmentId = departmentIds.get(0);
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO baseVO = new BaseVO();
            BeanUtils.copyProperties(departmentDO, baseVO);
            userGroupVO.setDepartment(baseVO);
        }
    }

    /**
     * 补充用户组区域信息
     *
     * @param userGroupVO
     */
    private void fillArea(UserGroupVO userGroupVO) {
        List<Long> areaIds = userGroupRelaAreaDOMapper.getAreaIdListByUserGroupId(userGroupVO.getId());
        if (CollectionUtils.isEmpty(areaIds)) {
            return;
        }
        Long areaId = areaIds.get(0);
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {
            BaseVO baseVO = new BaseVO();
            baseVO.setId(baseAreaDO.getAreaId());
            baseVO.setName(baseAreaDO.getAreaName());
            userGroupVO.setArea(baseVO);
        }
    }

    /**
     * 填充员工部门信息
     *
     * @param employeeVO
     */
    private void fillDepartment(EmployeeVO employeeVO) {
        List<Long> departmentIds = employeeRelaDepartmentDOMapper.getDepartmentIdListByEmployeeId(employeeVO.getId());
        if (CollectionUtils.isEmpty(departmentIds)) {
            return;
        }
        Long departmentId = departmentIds.get(0);
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO baseVO = new BaseVO();
            BeanUtils.copyProperties(departmentDO, baseVO);
            employeeVO.setDepartment(baseVO);
        }
    }

    /**
     * 填充员工直接主管信息
     *
     * @param parentId
     * @param employeeVO
     */
    private void fillLeader(Long parentId, EmployeeVO employeeVO) {
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
        if (null == employeeDO) {
            return;
        }
        BaseVO baseVO = new BaseVO();
        BeanUtils.copyProperties(employeeDO, baseVO);
        employeeVO.setLeader(baseVO);
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
                        employeeRelaUserGroupDO.setEmployeeId(employeeId);
                        employeeRelaUserGroupDO.setUserGroupId(id);
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
     * @param id
     * @param employeeIds
     * @return
     */
    private List<Long> distinctEmployeeIds(Long id, String employeeIds) {
        List<Long> existEmployeeIdList = employeeRelaUserGroupDOMapper.getEmployeeIdListByUserGroupId(id);
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
    public ResultBean<Void> bindAuth(Long id, String authIds) {
        return null;
    }

    @Override
    public ResultBean<Void> unbindAuth(Long id, String authIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(authIds), "权限ID列表不能为空");


        return null;
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
    private void updateDepartment(Long userGroupId, Long departmentId) {
        // get
        List<DepartmentRelaUserGroupDO> departmentRelaUserGroupDOS = departmentRelaUserGroupDOMapper.getByUserGroupId(userGroupId);
        if (CollectionUtils.isEmpty(departmentRelaUserGroupDOS)) {
            // insert
            DepartmentRelaUserGroupDO departmentRelaUserGroupDO = new DepartmentRelaUserGroupDO();
            departmentRelaUserGroupDO.setUserGroupId(userGroupId);
            departmentRelaUserGroupDO.setDepartmentId(departmentId);
            departmentRelaUserGroupDO.setGmtCreate(new Date());
            departmentRelaUserGroupDO.setGmtModify(new Date());
            int count = departmentRelaUserGroupDOMapper.insert(departmentRelaUserGroupDO);
            Preconditions.checkArgument(count > 0, "编辑部门失败");
        } else {
            // update : del + insert
            // delete old
            DepartmentRelaUserGroupDO departmentRelaUserGroupDO = departmentRelaUserGroupDOS.get(0);
            int delCount = departmentRelaUserGroupDOMapper.deleteByPrimaryKey(departmentRelaUserGroupDO);
            Preconditions.checkArgument(delCount > 0, "编辑部门失败");

            // insert new
            departmentRelaUserGroupDO.setDepartmentId(departmentId);
            departmentRelaUserGroupDO.setGmtModify(new Date());
            int count = departmentRelaUserGroupDOMapper.insert(departmentRelaUserGroupDO);
            Preconditions.checkArgument(count > 0, "编辑部门失败");
        }
    }

    /**
     * 编辑区域(城市)
     *
     * @param userGroupId
     * @param areaId
     */
    private void updateArea(Long userGroupId, Long areaId) {
        // get
        List<UserGroupRelaAreaDO> userGroupRelaAreaDOS = userGroupRelaAreaDOMapper.getByUserGroupId(userGroupId);
        if (CollectionUtils.isEmpty(userGroupRelaAreaDOS)) {
            // insert
            UserGroupRelaAreaDO userGroupRelaAreaDO = new UserGroupRelaAreaDO();
            userGroupRelaAreaDO.setUserGroupId(userGroupId);
            userGroupRelaAreaDO.setAreaId(areaId);
            userGroupRelaAreaDO.setGmtCreate(new Date());
            userGroupRelaAreaDO.setGmtModify(new Date());
            int count = userGroupRelaAreaDOMapper.insert(userGroupRelaAreaDO);
            Preconditions.checkArgument(count > 0, "编辑区域失败");
        } else {
            // update : del + insert
            // delete old
            UserGroupRelaAreaDO userGroupRelaAreaDO = userGroupRelaAreaDOS.get(0);
            int delCount = userGroupRelaAreaDOMapper.deleteByPrimaryKey(userGroupRelaAreaDO);
            Preconditions.checkArgument(delCount > 0, "编辑区域失败");

            // insert new
            userGroupRelaAreaDO.setAreaId(areaId);
            userGroupRelaAreaDO.setGmtModify(new Date());
            int insertCount = userGroupRelaAreaDOMapper.insert(userGroupRelaAreaDO);
            Preconditions.checkArgument(insertCount > 0, "编辑区域失败");
        }
    }

    /**
     * TODO 绑定权限列表
     *
     * @param userGroupId
     * @param authIdList
     */
    private void bindAuth(Long userGroupId, List<Long> authIdList) {


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
