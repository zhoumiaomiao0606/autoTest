package com.yunche.loan.service;

import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.LoginUserExtInfo;

/**
 * @author liuzhe
 * @date 2018/12/4
 */
public interface YuncheCloudExtService {

    LoginUserExtInfo getLoginUserExtInfoByLoginUserId(Long loginUserId);
}
