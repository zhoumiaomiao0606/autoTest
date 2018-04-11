package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.mapper.EmployeeDOMapper;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.vo.CascadeVO;
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
 * @author liuzhe
 * @date 2018/2/6
 */
@Component
public class EmployeeCache {

    private static final String EMPLOYEE_CASCADE_CACHE_KEY = "cascade:cache:employee";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmployeeDOMapper employeeDOMapper;


    public List<CascadeVO> get() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_CASCADE_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeVO.class);
        }

        // 刷新缓存
        refresh();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeVO.class);
        }
        return Collections.EMPTY_LIST;
    }

    @PostConstruct
    public void refresh() {
        // getAll
        List<EmployeeDO> employeeDOS = employeeDOMapper.getAll(VALID_STATUS);

        // parentId - DOS
        Map<Long, List<EmployeeDO>> parentIdDOMap = getParentIdDOSMapping(employeeDOS);

        // 分级递归解析
        List<CascadeVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_CASCADE_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(topLevelList));
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
    private List<CascadeVO> parseLevelByLevel(Map<Long, List<EmployeeDO>> parentIdDOMap) {
        if (!CollectionUtils.isEmpty(parentIdDOMap)) {
            List<EmployeeDO> parentDOS = parentIdDOMap.get(-1L);
            if (!CollectionUtils.isEmpty(parentDOS)) {
                List<CascadeVO> topLevelList = parentDOS.stream()
                        .filter(Objects::nonNull)
                        .map(p -> {
                            CascadeVO parent = new CascadeVO();
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
    private void fillChilds(CascadeVO parent, Map<Long, List<EmployeeDO>> parentIdDOMap) {
        List<EmployeeDO> childs = parentIdDOMap.get(parent.getValue());
        if (CollectionUtils.isEmpty(childs)) {
            return;
        }

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
                    fillChilds(child, parentIdDOMap);
                });
    }
}
