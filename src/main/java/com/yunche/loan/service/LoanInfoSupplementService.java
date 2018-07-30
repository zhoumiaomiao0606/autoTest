package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InfoSupplementParam;
import com.yunche.loan.domain.vo.InfoSupplementVO;
import com.yunche.loan.domain.vo.InfoSupplementVO2;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/25
 */
public interface LoanInfoSupplementService {

    ResultBean<Void> upload(InfoSupplementParam infoSupplementParam);

    ResultBean<InfoSupplementVO> detail__(Long id);

    ResultBean<List<InfoSupplementVO2>> history(Long orderId);

    ResultBean<InfoSupplementVO2> detail(Long infoSupplementId);

    ResultBean<Void> save(InfoSupplementParam infoSupplementParam);
}
