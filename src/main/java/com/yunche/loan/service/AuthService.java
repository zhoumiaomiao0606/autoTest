package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.AuthQuery;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.domain.viewObj.PageVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/29
 */
public interface AuthService {
    ResultBean<List<LevelVO>> listMenu();

    ResultBean<List<PageVO>> listOperation(AuthQuery query);

    ResultBean<List<PageVO>> listBindOperation(AuthQuery query);

    ResultBean<List<PageVO>> listUnbindOperation(AuthQuery query);
}
