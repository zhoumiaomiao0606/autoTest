package com.yunche.loan.service;

import com.yunche.loan.domain.param.ZhongAnCreditStructParam;
import com.zhongan.scorpoin.common.ZhongAnOpenException;

/**
 * @author liuzhe
 * @date 2019/2/21
 */
public interface ZhongAnService {

    Object creditStruct(ZhongAnCreditStructParam param) throws ZhongAnOpenException;
}
