package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InfoSupplementParam;
import com.yunche.loan.domain.vo.InfoSupplementVO;
import com.yunche.loan.domain.vo.UniversalInfoSupplementVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/25
 */
public interface LoanInfoSupplementService {

    @Deprecated
    ResultBean<Void> upload(InfoSupplementParam infoSupplementParam);

    @Deprecated
    ResultBean<InfoSupplementVO> detail__(Long id);

    ResultBean<List<UniversalInfoSupplementVO>> history(Long orderId);

    ResultBean<UniversalInfoSupplementVO> detail(Long infoSupplementId);

    ResultBean<Void> save(InfoSupplementParam infoSupplementParam);
}
