package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AuthQuery;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/1/29
 */
public interface AuthService {
    ResultBean<List<CascadeVO>> listMenu();

    ResultBean<List<PageVO>> listOperation(AuthQuery query);

    ResultBean<List<PageVO>> listBindOperation(AuthQuery query);

    ResultBean<List<PageVO>> listUnbindOperation(AuthQuery query);

    ResultBean<Map<String, Boolean>> listMenu_();
}
