package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.DepartmentDOMapper;
import com.yunche.loan.domain.QueryObj.DepartmentQuery;
import com.yunche.loan.domain.dataObj.DepartmentDO;
import com.yunche.loan.domain.param.DepartmentParam;
import com.yunche.loan.domain.viewObj.DepartmentVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.service.DepartmentService;
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
 * @date 2018/1/23
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentDOMapper departmentDOMapper;


    @Override
    public ResultBean<Long> create(DepartmentParam departmentParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(departmentParam.getName()), "部门名称不能为空");
//        Preconditions.checkNotNull(departmentParam.getParentId(), "上级部门不能为空");
        Preconditions.checkNotNull(departmentParam.getEmployeeId(), "部门负责人不能为空");

        // 创建实体，并返回ID
        Long id = insertAndGetId(departmentParam);

        // 绑定用户组(角色)列表
        bindUserGroup(id, departmentParam.getUserGroupIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> update(DepartmentDO departmentDO) {
        Preconditions.checkNotNull(departmentDO.getId(), "id不能为空");

        departmentDO.setGmtModify(new Date());
        int count = departmentDOMapper.updateByPrimaryKeySelective(departmentDO);
        Preconditions.checkArgument(count > 0, "编辑失败");
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = departmentDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<DepartmentVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(departmentDO, "id有误，数据不存在");

        DepartmentVO departmentVO = new DepartmentVO();
        BeanUtils.copyProperties(departmentDO, departmentVO);

        return ResultBean.ofSuccess(departmentVO);
    }

    @Override
    public ResultBean<List<DepartmentVO>> query(DepartmentQuery query) {
        int totalNum = departmentDOMapper.count(query);
        Preconditions.checkArgument(totalNum > 0, "无符合条件的数据");

        List<DepartmentDO> departmentDOS = departmentDOMapper.query(query);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(departmentDOS), "无符合条件的数据");

        List<DepartmentVO> departmentVOS = departmentDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    DepartmentVO departmentVO = new DepartmentVO();
                    BeanUtils.copyProperties(e, departmentVO);
                    return departmentVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(departmentVOS, totalNum, query.getPageIndex(), query.getPageSize());
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

        Map<Long, List<DepartmentDO>> parentIdDOMap = Maps.newConcurrentMap();
        departmentDOS.parallelStream()
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
    private void fillChilds(LevelVO parent, Map<Long, List<DepartmentDO>> parentIdDOMap) {
        List<DepartmentDO> childs = parentIdDOMap.get(parent.getId());
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
     * @param departmentParam
     * @return
     */
    private Long insertAndGetId(DepartmentParam departmentParam) {
        DepartmentDO departmentDO = new DepartmentDO();
        BeanUtils.copyProperties(departmentParam, departmentDO);
        departmentDO.setStatus(VALID_STATUS);
        departmentDO.setGmtCreate(new Date());
        departmentDO.setGmtModify(new Date());
        int count = departmentDOMapper.insertSelective(departmentDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return departmentDO.getId();
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
