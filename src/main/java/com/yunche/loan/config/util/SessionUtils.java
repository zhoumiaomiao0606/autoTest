package com.yunche.loan.config.util;

import com.yunche.loan.config.constant.BaseExceptionEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.EmployeeDO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
public class SessionUtils {

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static EmployeeDO getLoginUser() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (null == principal) {
            SecurityUtils.getSubject().logout();
            throw new BizException(BaseExceptionEnum.NOT_LOGIN.getCode(), BaseExceptionEnum.NOT_LOGIN.getMessage());
        } else {
            EmployeeDO loginUser = new EmployeeDO();
            BeanUtils.copyProperties(principal, loginUser);
            return loginUser;
        }
    }
}
