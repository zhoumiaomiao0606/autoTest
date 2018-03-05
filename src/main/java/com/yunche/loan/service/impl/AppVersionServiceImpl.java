package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.cache.AppVersionCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.AppVersionDOMapper;
import com.yunche.loan.domain.entity.AppVersionDO;
import com.yunche.loan.domain.query.AppVersionQuery;
import com.yunche.loan.domain.vo.AppVersionVO;
import com.yunche.loan.service.AppVersionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AppVersionConst.IS_LATEST_VERSION;
import static com.yunche.loan.config.constant.AppVersionConst.NOT_LATEST_VERSION;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/2/12
 */
@Service
@Transactional
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppVersionDOMapper appVersionDOMapper;
    @Autowired
    private AppVersionCache appVersionCache;


    @Override
    public ResultBean<Long> create(AppVersionDO appVersionDO) {
        Preconditions.checkArgument(null != appVersionDO && StringUtils.isNotBlank(appVersionDO.getVersionCode()), "版本号不能为空");
        Preconditions.checkNotNull(appVersionDO.getTerminalType(), "终端类型不能为空");
        Preconditions.checkNotNull(appVersionDO.getUpdateType(), "更新类型不能为空");
        Preconditions.checkNotNull(appVersionDO.getStatus(), "状态不能为空");

        // update all latest_version
        updateAllLatestVersion(appVersionDO.getTerminalType());

        appVersionDO.setGmtCreate(new Date());
        appVersionDO.setGmtModify(new Date());
        appVersionDO.setIsLatestVersion(IS_LATEST_VERSION);
        int count = appVersionDOMapper.insertSelective(appVersionDO);
        Preconditions.checkArgument(count > 0, "新增失败");

        // 刷新版本缓存
        appVersionCache.refresh(appVersionDO.getTerminalType());

        return ResultBean.ofSuccess(appVersionDO.getId(), "新增成功");
    }

    @Override
    public ResultBean<Void> update(AppVersionDO appVersionDO) {
        Preconditions.checkNotNull(appVersionDO.getId(), "id不能为空");

        appVersionDO.setGmtModify(new Date());
        int count = appVersionDOMapper.updateByPrimaryKeySelective(appVersionDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        // 刷新版本缓存
        appVersionCache.refresh(appVersionDO.getTerminalType());

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<AppVersionVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        AppVersionDO appVersionDO = appVersionDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(appVersionDO, "id有误，数据不存在");

        AppVersionVO appVersionVO = new AppVersionVO();
        BeanUtils.copyProperties(appVersionDO, appVersionVO);

        return ResultBean.ofSuccess(appVersionVO);
    }

    @Override
    public ResultBean<List<AppVersionVO>> query(AppVersionQuery query) {
        int totalNum = appVersionDOMapper.count(query);
        if (totalNum > 0) {

            List<AppVersionDO> appVersionDOS = appVersionDOMapper.query(query);
            if (!CollectionUtils.isEmpty(appVersionDOS)) {

                List<AppVersionVO> appVersionVOS = appVersionDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            AppVersionVO appVersionVO = new AppVersionVO();
                            BeanUtils.copyProperties(e, appVersionVO);

                            return appVersionVO;
                        })
                        .sorted(Comparator.comparing(AppVersionVO::getId))
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(appVersionVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<AppVersionVO.Update> checkUpdate(AppVersionDO appVersionDO) {
        Preconditions.checkArgument(StringUtils.isNotBlank(appVersionDO.getVersionCode()), "版本号不能为空");
        Preconditions.checkNotNull(appVersionDO.getTerminalType(), "终端类型不能为空");

        // 获取最后一个版本
        AppVersionDO latestVersionDO = appVersionCache.getLatestVersion(appVersionDO.getTerminalType());
        Preconditions.checkNotNull(latestVersionDO, "获取最新版本失败");

        // 最新版本
        if (appVersionDO.getVersionCode().equals(latestVersionDO.getVersionCode())) {
            AppVersionVO.Update updateVO = new AppVersionVO.Update();
            updateVO.setNeedUpdate(false);
            updateVO.setLatestVersion(null);
            return ResultBean.ofSuccess(updateVO, "当前版本已经是最新版本");
        }

        // 不是最新版本
        AppVersionVO latestVersionVO = new AppVersionVO();
        BeanUtils.copyProperties(latestVersionDO, latestVersionVO);
        AppVersionVO.Update updateVO = new AppVersionVO.Update();
        updateVO.setNeedUpdate(true);
        updateVO.setLatestVersion(latestVersionVO);

        return ResultBean.ofSuccess(updateVO, "发现新版本");
    }

    /**
     * 将同一终端的所有is_latest_version置为false
     *
     * @param terminalType
     */
    private void updateAllLatestVersion(Byte terminalType) {
        AppVersionDO appVersionDO = new AppVersionDO();
        appVersionDO.setTerminalType(terminalType);
        appVersionDO.setIsLatestVersion(NOT_LATEST_VERSION);
        appVersionDO.setStatus(VALID_STATUS);
        appVersionDO.setGmtModify(new Date());
        appVersionDOMapper.updateLatestVersion(appVersionDO);
    }
}
