package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.EmployeeDOMapper;
import com.yunche.loan.domain.QueryObj.EmployeeQuery;
import com.yunche.loan.domain.dataObj.DepartmentDO;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.viewObj.DepartmentVO;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDOMapper employeeDOMapper;


    @Override
    public ResultBean<Long> create(EmployeeParam employeeParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getName()), "姓名不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getIdCard()), "身份证号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getMobile()), "手机号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getEmail()), "电子邮箱不能为空");

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

        return ResultBean.ofSuccess(employeeVO);
    }

    @Override
    public ResultBean<List<EmployeeVO>> query(EmployeeQuery query) {
        int totalNum = employeeDOMapper.count(query);
        Preconditions.checkArgument(totalNum > 0, "无符合条件的数据");

        List<EmployeeDO> employeeDOS = employeeDOMapper.query(query);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(employeeDOS), "无符合条件的数据");

        List<EmployeeVO> employeeVOS = employeeDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    EmployeeVO employeeVO = new EmployeeVO();
                    BeanUtils.copyProperties(e, employeeVO);
                    return employeeVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(employeeVOS, totalNum, query.getPageIndex(), query.getPageSize());
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
                            BeanUtils.copyProperties(p, parent);

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
        List<EmployeeDO> childs = parentIdDOMap.get(parent.getId());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

        childs.stream()
                .forEach(c -> {
                    LevelVO child = new LevelVO();
                    BeanUtils.copyProperties(c, child);

                    List<LevelVO> childList = parent.getChildList();
                    if (CollectionUtils.isEmpty(childList)) {
                        parent.setChildList(Lists.newArrayList(child));
                    } else {
                        parent.getChildList().add(child);
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
        employeeDO.setStatus(VALID_STATUS);
        employeeDO.setGmtCreate(new Date());
        employeeDO.setGmtModify(new Date());
        int count = employeeDOMapper.insertSelective(employeeDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return employeeDO.getId();
    }

    /**
     * TODO 绑定用户组(角色)列表
     *
     * @param id
     * @param userGroupIdList
     */
    private void bindUserGroup(Long id, List<Long> userGroupIdList) {
        if (CollectionUtils.isEmpty(userGroupIdList)) {
            return;
        }

        // 去重

        // 绑定

    }
}
