package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yunche.loan.dao.mapper.AuthDOMapper;
import com.yunche.loan.dao.mapper.MenuDOMapper;
import com.yunche.loan.dao.mapper.OperationDOMapper;
import com.yunche.loan.dao.mapper.PageDOMapper;
import com.yunche.loan.domain.dataObj.AuthDO;
import com.yunche.loan.domain.dataObj.MenuDO;
import com.yunche.loan.domain.dataObj.OperationDO;
import com.yunche.loan.domain.dataObj.PageDO;
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

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/2/8
 */
@Component
public class AuthCache {

    /**
     * ID - DO
     */
    public static final String AUTH_MAP_CACHE = "auth:cache:auth";
    public static final String AUTH_MENU_MAP_CACHE = "auth:cache:menu";
    public static final String AUTH_PAGE_MAP_CACHE = "auth:cache:page";
    public static final String AUTH_OPERATION_MAP_CACHE = "auth:cache:operation";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AuthDOMapper authDOMapper;
    @Autowired
    private MenuDOMapper menuDOMapper;
    @Autowired
    private PageDOMapper pageDOMapper;
    @Autowired
    private OperationDOMapper operationDOMapper;


    /**
     * 获取权限列表
     *
     * @return
     */
    public Map<Long, AuthDO> getAuth() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_MAP_CACHE);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新auth缓存
        refreshAuth();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 刷新auth缓存
     */
//    @PostConstruct
    private void refreshAuth() {
        List<AuthDO> allAuth = authDOMapper.getAll();
        if (!CollectionUtils.isEmpty(allAuth)) {

            Map<Long, AuthDO> idDOMap = Maps.newConcurrentMap();
            allAuth.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idDOMap.put(e.getId(), e);
                    });

            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_MAP_CACHE);
            boundValueOps.set(JSON.toJSONString(idDOMap));
        }
    }

    /**
     * 获取MENU列表
     *
     * @return
     */
    public Map<Long, MenuDO> getMenu() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_MENU_MAP_CACHE);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新auth缓存
        refreshMenu();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 刷新Menu缓存
     */
//    @PostConstruct
    private void refreshMenu() {
        List<MenuDO> allMenu = menuDOMapper.getAll(VALID_STATUS);
        if (!CollectionUtils.isEmpty(allMenu)) {

            Map<Long, MenuDO> idDOMap = Maps.newConcurrentMap();
            allMenu.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idDOMap.put(e.getId(), e);
                    });

            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_MENU_MAP_CACHE);
            boundValueOps.set(JSON.toJSONString(idDOMap));
        }
    }

    /**
     * 获取PAGE列表
     *
     * @return
     */
    public Map<Long, PageDO> getPage() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_PAGE_MAP_CACHE);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新PAGE缓存
        refreshPage();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 刷新PAGE缓存
     */
//    @PostConstruct
    private void refreshPage() {
        List<PageDO> allPage = pageDOMapper.getAll(VALID_STATUS);
        if (!CollectionUtils.isEmpty(allPage)) {

            Map<Long, PageDO> idDOMap = Maps.newConcurrentMap();
            allPage.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idDOMap.put(e.getId(), e);
                    });

            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_PAGE_MAP_CACHE);
            boundValueOps.set(JSON.toJSONString(idDOMap));
        }
    }

    /**
     * 获取OPERATION列表
     *
     * @return
     */
    public Map<Long, OperationDO> getOperation() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_OPERATION_MAP_CACHE);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }

        // 刷新OPERATION缓存
        refreshOperation();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, Map.class);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 刷新OPERATION缓存
     */
//    @PostConstruct
    private void refreshOperation() {
        List<OperationDO> allOperation = operationDOMapper.getAll(VALID_STATUS);
        if (!CollectionUtils.isEmpty(allOperation)) {

            Map<Long, OperationDO> idDOMap = Maps.newConcurrentMap();
            allOperation.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        idDOMap.put(e.getId(), e);
                    });

            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(AUTH_OPERATION_MAP_CACHE);
            boundValueOps.set(JSON.toJSONString(idDOMap));
        }
    }
}
