package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.mapper.DepartmentDOMapper;
import com.yunche.loan.domain.entity.DepartmentDO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.mapper.PartnerDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * 部门缓存
 *
 * @author liuzhe
 * @date 2018/2/2
 */
@Component
public class DepartmentCache {

    private static final String DEPARTMENT_CASCADE_CACHE_KEY = "department:cache:cascade";

    private static final String DATA_FLOW_FLOW_DEPT_CACHE_KEY = "data-flow:cache:flow-dept";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DepartmentDOMapper departmentDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;


    /**
     * 级联部门列表
     *
     * @return
     */
    public List<CascadeVO> get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(DEPARTMENT_CASCADE_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeVO.class);
        }

        // 刷新缓存
        refreshCascadeDept();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeVO.class);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 资料流转 -流进/出部门(含合伙人)
     *
     * @return
     */
    public List<BaseVO> getFlowDept() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(DATA_FLOW_FLOW_DEPT_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, BaseVO.class);
        }

        // 刷新缓存
        refreshFlowDept();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, BaseVO.class);
        }
        return Collections.EMPTY_LIST;
    }

    @PostConstruct
    public void refresh() {
        refreshCascadeDept();
        refreshFlowDept();
    }

    private void refreshCascadeDept() {
        // getAll
        List<DepartmentDO> departmentDOS = departmentDOMapper.getAll(VALID_STATUS);

        // parentId - DOS
        Map<Long, List<DepartmentDO>> parentIdDOMap = getParentIdDOSMapping(departmentDOS);

        // 分级递归解析
        List<CascadeVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(DEPARTMENT_CASCADE_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(topLevelList));
    }

    private void refreshFlowDept() {
        // 部门
        List<DepartmentDO> departmentDOS = departmentDOMapper.getAll(VALID_STATUS);

        List<BaseVO> deptList = departmentDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {

                    BaseVO baseVO = new BaseVO();

                    baseVO.setId(e.getId());
                    baseVO.setName(e.getName());

                    return baseVO;
                })
                .collect(Collectors.toList());

        // 合伙人
        List<PartnerDO> partnerDOS = partnerDOMapper.getAll(VALID_STATUS);

        List<BaseVO> partnerList = partnerDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {

                    BaseVO baseVO = new BaseVO();

                    baseVO.setId(e.getId());
                    baseVO.setName(e.getName());

                    return baseVO;
                })
                .collect(Collectors.toList());

        deptList.addAll(partnerList);
        deptList.removeAll(Collections.singleton(null));

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(DATA_FLOW_FLOW_DEPT_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(deptList));
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
    private List<CascadeVO> parseLevelByLevel(Map<Long, List<DepartmentDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<DepartmentDO> parentDOS = parentIdDOMap.get(-1L);
            if (!CollectionUtils.isEmpty(parentDOS)) {
                List<CascadeVO> topLevelList = parentDOS.stream()
                        .map(p -> {
                            CascadeVO parent = new CascadeVO();
                            parent.setValue(p.getId());
                            parent.setLabel(p.getName());
                            parent.setLevel(p.getLevel());

                            // 递归填充子列表
                            fillChilds(parent, parentIdDOMap, 10);
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
     * @param limit
     */
    private void fillChilds(CascadeVO parent, Map<Long, List<DepartmentDO>> parentIdDOMap, int limit) {
        limit--;
        if (limit < 0) {
            return;
        }
        List<DepartmentDO> childs = parentIdDOMap.get(parent.getValue());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

        int finalLimit = limit;
        childs.stream()
                .forEach(c -> {
                    CascadeVO child = new CascadeVO();
                    child.setValue(c.getId());
                    child.setLabel(c.getName());
                    child.setLevel(c.getLevel());

                    List<CascadeVO> childList = parent.getChildren();
                    if (CollectionUtils.isEmpty(childList)) {
                        parent.setChildren(Lists.newArrayList(child));
                    } else {
                        parent.getChildren().add(child);
                    }

                    // 递归填充子列表
                    fillChilds(child, parentIdDOMap, finalLimit);
                });
    }
}
