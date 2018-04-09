package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.DepartmentCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.*;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.DepartmentQuery;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.DepartmentParam;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.DepartmentVO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.UserGroupVO;
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
    @Autowired
    private DepartmentCache departmentCache;


    @Override
    public ResultBean<Long> create(DepartmentParam departmentParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(departmentParam.getName()), "部门名称不能为空");
        Preconditions.checkNotNull(departmentParam.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(departmentParam.getStatus()) || INVALID_STATUS.equals(departmentParam.getStatus()),
                "状态非法");

        // 创建实体，并返回ID
        Long id = insertAndGetId(departmentParam);

        // 绑定用户组(角色)列表
        doBindUserGroup(id, departmentParam.getUserGroupIdList());

        // 刷新缓存
        departmentCache.refresh();

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        checkHasChilds(id);

        int count = departmentDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        // 刷新缓存
        departmentCache.refresh();

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> update(DepartmentDO departmentDO) {
        Preconditions.checkNotNull(departmentDO.getId(), "id不能为空");
        Preconditions.checkArgument(!departmentDO.getId().equals(departmentDO.getParentId()), "上级部门不能为自身");

        // 校验是否是删除操作
        checkIfDel(departmentDO);

        // level
        Long parentId = departmentDO.getParentId();
        if (null != parentId) {
            DepartmentDO parentDepartmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != parentDepartmentDO) {
                Integer parentLevel = parentDepartmentDO.getLevel();
                Integer level = parentLevel == null ? null : parentLevel + 1;
                departmentDO.setLevel(level);
            }
        }

        departmentDO.setGmtModify(new Date());
        int count = departmentDOMapper.updateByPrimaryKeySelective(departmentDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        // 刷新缓存
        departmentCache.refresh();

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<DepartmentVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(id, null);
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
    public ResultBean<List<CascadeVO>> listAll() {
        // 走缓存
        List<CascadeVO> cascadeVOS = departmentCache.get();
        return ResultBean.ofSuccess(cascadeVOS);
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

        // convert
        List<Long> userGroupIdList = Arrays.asList(userGroupIds.split(",")).stream()
                .map(e -> {
                    return Long.valueOf(e);
                })
                .collect(Collectors.toList());

        // 绑定用户组
        doBindUserGroup(id, userGroupIdList);

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
    private void doBindUserGroup(Long departmentId, List<Long> userGroupIdList) {
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
        Preconditions.checkArgument(CollectionUtils.isEmpty(departmentDOS), "请先删除所有下级部门");
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
        fillEmployeeNum(departmentVO);
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
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO parentDepartment = new BaseVO();
            BeanUtils.copyProperties(departmentDO, parentDepartment);
            // 递归填充所有上层父级部门
            fillSupperDepartment(departmentDO.getParentId(), Lists.newArrayList(parentDepartment), departmentVO);
        }
    }

    /**
     * 递归填充所有上层父级部门
     *
     * @param parentId
     * @param supperDepartmentList
     * @param departmentVO
     */
    private void fillSupperDepartment(Long parentId, List<BaseVO> supperDepartmentList, DepartmentVO departmentVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                BaseVO parentDepartment = new BaseVO();
                BeanUtils.copyProperties(departmentDO, parentDepartment);
                supperDepartmentList.add(parentDepartment);
                fillSupperDepartment(departmentDO.getParentId(), supperDepartmentList, departmentVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(supperDepartmentList);
            departmentVO.setParent(supperDepartmentList);
        }
    }

    private void fillLeader(Long employeeId, DepartmentVO departmentVO) {
        if (null == employeeId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(employeeId, VALID_STATUS);
        if (null != employeeDO) {
            BaseVO parentEmployee = new BaseVO();
            BeanUtils.copyProperties(employeeDO, parentEmployee);
            // 递归填充所有上层父级leader
            fillSupperLeader(employeeDO.getParentId(), Lists.newArrayList(parentEmployee), departmentVO);
        }
    }

    private void fillSupperLeader(Long parentId, List<BaseVO> supperLeaderList, DepartmentVO departmentVO) {
        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                BaseVO parentEmployee = new BaseVO();
                BeanUtils.copyProperties(employeeDO, parentEmployee);
                supperLeaderList.add(parentEmployee);
                fillSupperLeader(employeeDO.getParentId(), supperLeaderList, departmentVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(supperLeaderList);
            departmentVO.setLeader(supperLeaderList);
        }
    }

    private void fillArea(Long areaId, DepartmentVO departmentVO) {
        if (null == areaId) {
            return;
        }
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {
            BaseVO parentArea = new BaseVO();
            parentArea.setId(baseAreaDO.getAreaId());
            parentArea.setName(baseAreaDO.getAreaName());
            // 递归填充所有上层父级部门
            fillSupperArea(baseAreaDO.getParentAreaId(), Lists.newArrayList(parentArea), departmentVO);
        }
    }

    /**
     * @param parentId
     * @param supperAreaList
     * @param departmentVO
     */
    private void fillSupperArea(Long parentId, List<BaseVO> supperAreaList, DepartmentVO departmentVO) {
        if (null != parentId) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != baseAreaDO) {
                BaseVO parentArea = new BaseVO();
                parentArea.setId(baseAreaDO.getAreaId());
                parentArea.setName(baseAreaDO.getAreaName());
                supperAreaList.add(parentArea);
                fillSupperArea(baseAreaDO.getParentAreaId(), supperAreaList, departmentVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(supperAreaList);
            departmentVO.setArea(supperAreaList);
        }
    }

    /**
     * 填充部门人数
     *
     * @param departmentVO
     */
    private void fillEmployeeNum(DepartmentVO departmentVO) {
        EmployeeQuery query = new EmployeeQuery();
        query.setDepartmentId(departmentVO.getId());
        query.setStatus(VALID_STATUS);
        int employeeNum = employeeDOMapper.count(query);
        departmentVO.setEmployeeNum(employeeNum);
    }
}
