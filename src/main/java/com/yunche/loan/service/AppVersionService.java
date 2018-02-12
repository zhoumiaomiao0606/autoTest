package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.AppVersionDO;
import com.yunche.loan.domain.queryObj.AppVersionQuery;
import com.yunche.loan.domain.viewObj.AppVersionVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/12
 */
public interface AppVersionService {
    ResultBean<Long> create(AppVersionDO appVersionDO);

    ResultBean<Void> update(AppVersionDO appVersionDO);

    ResultBean<AppVersionVO> getById(Long id);

    ResultBean<List<AppVersionVO>> query(AppVersionQuery query);

    ResultBean<AppVersionVO.Update> checkUpdate(AppVersionDO appVersionDO);
}
