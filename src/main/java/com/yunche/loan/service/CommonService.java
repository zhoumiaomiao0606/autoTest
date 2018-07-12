package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.DataDictionaryVO;

/**
 * @author liuzhe
 * @date 2018/7/10
 */
public interface CommonService {

    ResultBean<DataDictionaryVO> dictionary() throws Exception;
}
