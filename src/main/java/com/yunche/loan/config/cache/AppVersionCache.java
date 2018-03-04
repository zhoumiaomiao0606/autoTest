package com.yunche.loan.config.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.yunche.loan.dao.AppVersionDOMapper;
import com.yunche.loan.domain.entity.AppVersionDO;
import com.yunche.loan.domain.query.AppVersionQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.yunche.loan.config.constant.AppVersionConst.*;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/2/12
 */
@Component
public class AppVersionCache {

    /**
     * IOS最新版本缓存KEY
     */
    private static final String APP_LATEST_VERSION_IOS_KEY = "app:cache:latest_version:ios";
    /**
     * Android最新版本缓存KEY
     */
    private static final String APP_LATEST_VERSION_ANDROID_KEY = "app:cache:latest_version:android";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AppVersionDOMapper appVersionDOMapper;


    /**
     * 获取最后一个版本信息
     *
     * @param terminalType 终端类型 1：ios；2：android
     * @return
     */
    public AppVersionDO getLatestVersion(Byte terminalType) {
        Preconditions.checkNotNull(terminalType, "终端类型不能为空");

        String key = null;
        if (terminalType.equals(TERMINAL_TYPE_IOS)) {
            key = APP_LATEST_VERSION_IOS_KEY;
        } else if (terminalType.equals(TERMINAL_TYPE_ANDROID)) {
            key = APP_LATEST_VERSION_ANDROID_KEY;
        } else {
            throw new IllegalArgumentException("终端类型非法");
        }

        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, AppVersionDO.class);
        }

        // 刷新缓存
        refresh(terminalType);

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, AppVersionDO.class);
        }
        return null;
    }

    /**
     * 刷新缓存
     *
     * @param terminalType 终端类型 1：ios；2：android
     */
    public void refresh(Byte terminalType) {
        Preconditions.checkNotNull(terminalType, "终端类型不能为空");

        AppVersionQuery query = new AppVersionQuery();
        query.setTerminalType(terminalType);
        query.setIsLatestVersion(IS_LATEST_VERSION);
        query.setStatus(VALID_STATUS);
        List<AppVersionDO> appVersionDOS = appVersionDOMapper.query(query);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(appVersionDOS), "获取最新版本失败");

        String key = null;
        if (TERMINAL_TYPE_IOS.equals(terminalType)) {
            key = APP_LATEST_VERSION_IOS_KEY;
        } else if (TERMINAL_TYPE_ANDROID.equals(terminalType)) {
            key = APP_LATEST_VERSION_ANDROID_KEY;
        } else {
            throw new IllegalArgumentException("终端类型非法");
        }

        AppVersionDO latestVersionDO = appVersionDOS.get(0);
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);
        boundValueOps.set(JSON.toJSONString(latestVersionDO));
    }
}
