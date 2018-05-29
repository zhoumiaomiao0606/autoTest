package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.mapper.EmployeeDOMapper;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.vo.CascadeVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.EmployeeConst.TYPE_WB;
import static com.yunche.loan.config.constant.EmployeeConst.TYPE_ZS;

/**
 * @author liuzhe
 * @date 2018/2/6
 */
@Component
public class EmployeeCache {

    /**
     * 正式员工缓存
     */
    private static final String EMPLOYEE_ZS_CASCADE_CACHE_KEY = "cascade:cache:employee:zs";
    /**
     * 外包员工缓存
     */
    private static final String EMPLOYEE_WB_CASCADE_CACHE_KEY = "cascade:cache:employee:wb";
    /**
     * 获取所有 级联子级
     */
    private static final String EMPLOYEE_CHILD_CASCADE_CACHE_KEY = "cascade:cache:employee:child";
    /**
     * ID-BaseDO缓存
     */
    private static final String EMPLOYEE_ALL_CACHE_KEY = "all:cache:employee";

    /**
     * 唯一属性 (身份证号、手机号、邮箱、钉钉)
     */
    private static final String EMPLOYEE_ALL_ONLY_PROPERTY_IDCARD_CACHE_KEY = "all:cache:employee:onlyProperty:id_card";
    private static final String EMPLOYEE_ALL_ONLY_PROPERTY_MOBILE_CACHE_KEY = "all:cache:employee:onlyProperty:mobile";
    private static final String EMPLOYEE_ALL_ONLY_PROPERTY_EMAIL_CACHE_KEY = "all:cache:employee:onlyProperty:email";
    private static final String EMPLOYEE_ALL_ONLY_PROPERTY_DINGDING_CACHE_KEY = "all:cache:employee:onlyProperty:ding_ding";

    public static final Byte ID_CARD = 1;

    public static final Byte MOBILE = 2;

    public static final Byte EMAIL = 3;

    public static final Byte DINGDING = 4;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;


    /**
     * 通过员工类型获取
     *
     * @param type 员工类型
     * @return
     */
    public List<CascadeVO> get(Byte type) {
        String cacheKey = null;
        if (TYPE_ZS.equals(type)) {
            cacheKey = EMPLOYEE_ZS_CASCADE_CACHE_KEY;
        } else if (TYPE_WB.equals(type)) {
            cacheKey = EMPLOYEE_WB_CASCADE_CACHE_KEY;
        }

        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(cacheKey);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeVO.class);
        }

        // 刷新缓存
        refresh(type, cacheKey);

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, CascadeVO.class);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 获取所有 级联子级ID列表
     *
     * @param parentId
     * @return
     */
    public List<Long> getCascadeChildIdList(Long parentId) {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_ALL_CACHE_KEY);
        String result = boundValueOps.get();

        if (StringUtils.isNotBlank(result)) {

            Map<String, JSONObject> idBaseDOMap = JSON.parseObject(result, Map.class);

            if (!CollectionUtils.isEmpty(idBaseDOMap)) {

                List<BaseVO> allEmployee = idBaseDOMap.values().stream()
                        .filter(Objects::nonNull)
                        .map(jsonObject -> {

                            BaseVO baseVO = JSON.toJavaObject(jsonObject, BaseVO.class);
                            return baseVO;
                        })
                        .collect(Collectors.toList());

                List<Long> cascadeChildIdList = Lists.newArrayList();

                fillCascadeChildIdList(allEmployee, parentId, cascadeChildIdList, 20);

                return cascadeChildIdList;
            }
        }

        refreshAll();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {

            Map<String, JSONObject> idBaseDOMap = JSON.parseObject(result, Map.class);

            if (!CollectionUtils.isEmpty(idBaseDOMap)) {

                List<BaseVO> allEmployee = idBaseDOMap.values().stream()
                        .filter(Objects::nonNull)
                        .map(jsonObject -> {

                            BaseVO baseVO = JSON.toJavaObject(jsonObject, BaseVO.class);
                            return baseVO;
                        })
                        .collect(Collectors.toList());

                List<Long> cascadeChildIdList = Lists.newArrayList();

                fillCascadeChildIdList(allEmployee, parentId, cascadeChildIdList, 20);

                return cascadeChildIdList;
            }
        }
        return null;
    }

    /**
     * 递归填充 级联子级ID列表
     *
     * @param allEmployee
     * @param parentId
     * @param cascadeChildIdList 级联子级ID容器
     * @param limit              递归限制次数
     */
    private void fillCascadeChildIdList(Collection<BaseVO> allEmployee, Long parentId, List<Long> cascadeChildIdList, int limit) {

        limit--;
        if (limit < 0) {
            return;
        }

        int finalLimit = limit;
        allEmployee.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Long selfId = e.getId();
                    Long parentId_ = e.getParentId();

                    if (parentId.equals(parentId_)) {
                        cascadeChildIdList.add(parentId_);
                        fillCascadeChildIdList(allEmployee, selfId, cascadeChildIdList, finalLimit);
                    }

                });
    }

    /**
     * 通过ID获取
     *
     * @param userId
     * @return
     */
    public BaseVO getById(Long userId) {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_ALL_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            Map<String, BaseVO> idBaseDOMap = JSON.parseObject(result, Map.class);
            if (!CollectionUtils.isEmpty(idBaseDOMap)) {
                BaseVO baseVO = idBaseDOMap.get(String.valueOf(userId));
                if (null != baseVO) {
                    return baseVO;
                }
            }
        }

        // 刷新缓存
        refreshAll();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            Map<String, BaseVO> idBaseDOMap = JSON.parseObject(result, Map.class);
            if (!CollectionUtils.isEmpty(idBaseDOMap)) {
                BaseVO baseVO = idBaseDOMap.get(String.valueOf(userId));
                if (null != baseVO) {
                    return baseVO;
                }
            }
        }
        return null;
    }

    /**
     * 唯一属性 (身份证号、手机号、邮箱、钉钉)
     *
     * @param type
     * @return
     */
    public List<String> getAllOnlyProperty(Byte type) {

        String key = null;
        if (ID_CARD.equals(type)) {
            key = EMPLOYEE_ALL_ONLY_PROPERTY_IDCARD_CACHE_KEY;
        } else if (MOBILE.equals(type)) {
            key = EMPLOYEE_ALL_ONLY_PROPERTY_MOBILE_CACHE_KEY;
        } else if (EMAIL.equals(type)) {
            key = EMPLOYEE_ALL_ONLY_PROPERTY_EMAIL_CACHE_KEY;
        } else if (DINGDING.equals(type)) {
            key = EMPLOYEE_ALL_ONLY_PROPERTY_DINGDING_CACHE_KEY;
        }

        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            List<String> onlyPropertyList = JSON.parseArray(result, String.class);
            return onlyPropertyList;
        }

        // 刷新缓存
        refreshAllOnlyProperty();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            List<String> onlyPropertyList = JSON.parseArray(result, String.class);
            return onlyPropertyList;
        }
        return null;
    }

    @PostConstruct
    public void refresh() {
        refresh(TYPE_ZS, EMPLOYEE_ZS_CASCADE_CACHE_KEY);
        refresh(TYPE_WB, EMPLOYEE_WB_CASCADE_CACHE_KEY);
        refreshAll();
        refreshAllOnlyProperty();
    }

    /**
     * 刷新
     *
     * @param type
     * @param cacheKey
     */
    public void refresh(Byte type, String cacheKey) {
        // getAll
        List<EmployeeDO> employeeDOS = employeeDOMapper.getAll(type, VALID_STATUS);

        // parentId - DOS
        Map<Long, List<EmployeeDO>> parentIdDOMap = getParentIdDOSMapping(employeeDOS);

        // 分级递归解析
        List<CascadeVO> topLevelList = parseLevelByLevel(parentIdDOMap);

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(cacheKey);
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

        Map<Long, List<EmployeeDO>> parentIdDOMap = Maps.newHashMap();

        employeeDOS.stream()
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
                            fillChilds(parent, parentIdDOMap, 20);
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
    private void fillChilds(CascadeVO parent, Map<Long, List<EmployeeDO>> parentIdDOMap, int limit) {
        limit--;
        if (limit < 0) {
            return;
        }

        List<EmployeeDO> childs = parentIdDOMap.get(parent.getValue());
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

    /**
     * ID-DO缓存
     */
    private void refreshAll() {
        Map<String, BaseVO> idDOMap = Maps.newHashMap();

        // getAll
        List<EmployeeDO> employeeDOS = employeeDOMapper.getAll(null, VALID_STATUS);
        if (!CollectionUtils.isEmpty(employeeDOS)) {
            employeeDOS.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        BaseVO baseVO = new BaseVO();
                        BeanUtils.copyProperties(e, baseVO);

                        idDOMap.put(String.valueOf(e.getId()), baseVO);
                    });
        }

        // 刷新缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_ALL_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(idDOMap));
    }

    /**
     * 唯一属性 (身份证号、手机号、邮箱、钉钉)
     */
    @PostConstruct
    private void refreshAllOnlyProperty() {

        // getAll
        List<EmployeeDO> allOnlyProperty = employeeDOMapper.getAllOnlyProperty();

        if (!CollectionUtils.isEmpty(allOnlyProperty)) {

            List<String> idCardList = Lists.newArrayList();
            List<String> mobileList = Lists.newArrayList();
            List<String> emailList = Lists.newArrayList();
            List<String> dingDingList = Lists.newArrayList();

            allOnlyProperty.stream()
                    .forEach(e -> {

                        String idCard = e.getIdCard();
                        String mobile = e.getMobile();
                        String email = e.getEmail();
                        String dingDing = e.getDingDing();

                        if (StringUtils.isNotBlank(idCard)) {
                            idCardList.add(idCard);
                        }
                        if (StringUtils.isNotBlank(mobile)) {
                            mobileList.add(mobile);
                        }
                        if (StringUtils.isNotBlank(email)) {
                            emailList.add(email);
                        }
                        if (StringUtils.isNotBlank(dingDing)) {
                            dingDingList.add(dingDing);
                        }

                    });

            // 刷新缓存
            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_ALL_ONLY_PROPERTY_IDCARD_CACHE_KEY);
            boundValueOps.set(JSON.toJSONString(idCardList));

            boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_ALL_ONLY_PROPERTY_MOBILE_CACHE_KEY);
            boundValueOps.set(JSON.toJSONString(mobileList));

            boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_ALL_ONLY_PROPERTY_EMAIL_CACHE_KEY);
            boundValueOps.set(JSON.toJSONString(emailList));

            boundValueOps = stringRedisTemplate.boundValueOps(EMPLOYEE_ALL_ONLY_PROPERTY_DINGDING_CACHE_KEY);
            boundValueOps.set(JSON.toJSONString(dingDingList));
        }
    }
}
