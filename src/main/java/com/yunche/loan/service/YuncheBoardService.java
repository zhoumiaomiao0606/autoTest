package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.YuncheBoardDO;
import com.yunche.loan.domain.param.YuncheBoardParam;

public interface YuncheBoardService {
    ResultBean<Long> create(YuncheBoardParam yuncheBoardParam);

    ResultBean<Void> update(YuncheBoardParam yuncheBoardParam);

    ResultBean listAll(YuncheBoardParam yuncheBoardParam);

    ResultBean<Void> delete(Integer id);

    YuncheBoardDO detail(Integer id);

    ResultBean listAllByLoginUser(YuncheBoardParam yuncheBoardParam);

    ResultBean board(YuncheBoardParam yuncheBoardParam);
}
