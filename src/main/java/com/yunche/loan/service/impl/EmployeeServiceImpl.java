package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.EmployeeQuery;
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
    private DepartmentRelaUserGroupDOMapper departmentRelaUserGroupDOMapper;
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

                            fillDepartment(userGroupVO);
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
        employeeDO.setGmtCreate(new Date());
        employeeDO.setGmtModify(new Date());
        int count = employeeDOMapper.insertSelective(employeeDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return employeeDO.getId();
    }

    /**
     * 绑定部门 -单个
     *
     * @param employeeId
     * @param departmentId
     */
//    private void bindDepartment(Long employeeId, Long departmentId) {
//        if (null == departmentId) {
//            return;
//        }
//
//        // check
//        EmployeeRelaDepartmentDOKey employeeRelaDepartmentDOKey = new EmployeeRelaDepartmentDOKey();
//        employeeRelaDepartmentDOKey.setEmployeeId(employeeId);
//        employeeRelaDepartmentDOKey.setDepartmentId(departmentId);
//        EmployeeRelaDepartmentDO employeeRelaDepartmentDO = employeeRelaDepartmentDOMapper.selectByPrimaryKey(employeeRelaDepartmentDOKey);
//
//        if (null == employeeRelaDepartmentDO) {
//            // insert
//            employeeRelaDepartmentDO.setEmployeeId(employeeId);
//            employeeRelaDepartmentDO.setDepartmentId(departmentId);
//            employeeRelaDepartmentDO.setGmtCreate(new Date());
//            employeeRelaDepartmentDO.setGmtModify(new Date());
//            int count = employeeRelaDepartmentDOMapper.insertSelective(employeeRelaDepartmentDO);
//            Preconditions.checkArgument(count > 0, "关联部门失败");
//        }
//    }

    /**
     * 填充员工部门信息
     *
     * @param departmentId
     * @param employeeVO
     */
    private void fillDepartment(Long departmentId, EmployeeVO employeeVO) {
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
    private void fillParent(Long parentId, EmployeeVO employeeVO) {
        if (null == parentId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
        if (null != employeeDO) {
            BaseVO baseVO = new BaseVO();
            BeanUtils.copyProperties(employeeDO, baseVO);
            employeeVO.setParent(baseVO);
        }
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
