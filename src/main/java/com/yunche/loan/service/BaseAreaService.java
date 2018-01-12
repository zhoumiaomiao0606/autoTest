package com.yunche.loan.service;

import com.yunche.loan.bo.BaseAreaBO;
import com.yunche.loan.result.ResultBOBean;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface BaseAreaService {

    ResultBOBean<BaseAreaBO> getById(Integer id);

}
